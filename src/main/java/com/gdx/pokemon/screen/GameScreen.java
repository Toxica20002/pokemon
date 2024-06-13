package com.gdx.pokemon.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gdx.pokemon.PokemonGame;
import com.gdx.pokemon.controller.*;
import com.gdx.pokemon.dialogue.ChoiceDialogueNode;
import com.gdx.pokemon.dialogue.Dialogue;
import com.gdx.pokemon.dialogue.LinearDialogueNode;
import com.gdx.pokemon.model.Camera;
import com.gdx.pokemon.model.DIRECTION;
import com.gdx.pokemon.model.Tile;
import com.gdx.pokemon.model.actor.Actor;
import com.gdx.pokemon.model.actor.PlayerActor;
import com.gdx.pokemon.model.world.World;
import com.gdx.pokemon.model.world.cutscene.CutsceneEvent;
import com.gdx.pokemon.model.world.cutscene.CutscenePlayer;
import com.gdx.pokemon.screen.renderer.EventQueueRenderer;
import com.gdx.pokemon.screen.renderer.NameRenderer;
import com.gdx.pokemon.screen.renderer.TileInfoRenderer;
import com.gdx.pokemon.screen.renderer.WorldRenderer;
import com.gdx.pokemon.screen.transition.FadeInTransition;
import com.gdx.pokemon.screen.transition.FadeOutTransition;
import com.gdx.pokemon.udp.UDP_client;
import com.gdx.pokemon.ui.DialogueBox;
import com.gdx.pokemon.ui.OptionBox;
import com.gdx.pokemon.util.Action;
import com.gdx.pokemon.util.AnimationSet;
import org.lwjgl.Sys;

import java.util.*;

public class GameScreen extends AbstractScreen implements CutscenePlayer {

	private static GameScreen instance = null;

	private InputMultiplexer multiplexer;
	private DialogueController dialogueController;
	private ActorMovementController playerController;
	private InteractionController interactionController;
	private OptionBoxController debugController;
	private TempController tempController;
	
	private HashMap<String, World> worlds = new HashMap<String, World>();
	private World world;
	private PlayerActor player;
	private HashMap<String, PlayerActor> players = new HashMap<String, PlayerActor>();
	private HashMap<String, TempController> tempControllers = new HashMap<String, TempController>();
	private Camera camera;
	private Dialogue dialogue;
	
	/* cutscenes */
	private Queue<CutsceneEvent> eventQueue = new ArrayDeque<CutsceneEvent>();
	private CutsceneEvent currentEvent;
	
	private SpriteBatch batch;
	
	private Viewport gameViewport;
	
	private WorldRenderer worldRenderer;
	private EventQueueRenderer queueRenderer; // renders cutscenequeue
	private TileInfoRenderer tileInfoRenderer;
	private NameRenderer nameRenderer;
	private boolean renderTileInfo = false;
	
	private int uiScale = 2;
	
	private Stage uiStage;
	private Table dialogRoot;	// root table used for dialogues
	private Table menuRoot;		// root table used for menus (i.e. debug menu)
	private DialogueBox dialogueBox;
	private OptionBox optionsBox;
	private OptionBox debugBox;

	private UDP_client udpClient = UDP_client.getInstance();

	private AnimationSet animations;

	private boolean isBattle = false;

	private String opponentAddress;

	private GameScreen(PokemonGame app) {
		super(app);
		gameViewport = new ScreenViewport();
		batch = new SpriteBatch();
		
		TextureAtlas atlas = app.getAssetManager().get("res/graphics_packed/tiles/tilepack.atlas", TextureAtlas.class);
		
		animations = new AnimationSet(
				new Animation(0.4f/2f, atlas.findRegions("brendan_walk_north"), PlayMode.LOOP_PINGPONG),
				new Animation(0.4f/2f, atlas.findRegions("brendan_walk_south"), PlayMode.LOOP_PINGPONG),
				new Animation(0.4f/2f, atlas.findRegions("brendan_walk_east"), PlayMode.LOOP_PINGPONG),
				new Animation(0.4f/2f, atlas.findRegions("brendan_walk_west"), PlayMode.LOOP_PINGPONG),
				atlas.findRegion("brendan_stand_north"),
				atlas.findRegion("brendan_stand_south"),
				atlas.findRegion("brendan_stand_east"),
				atlas.findRegion("brendan_stand_west")
		);
		animations.addBiking(
				new Animation(0.4f/2f, atlas.findRegions("brendan_bike_north"), PlayMode.LOOP_PINGPONG), 
				new Animation(0.4f/2f, atlas.findRegions("brendan_bike_south"), PlayMode.LOOP_PINGPONG), 
				new Animation(0.4f/2f, atlas.findRegions("brendan_bike_east"), PlayMode.LOOP_PINGPONG), 
				new Animation(0.4f/2f, atlas.findRegions("brendan_bike_west"), PlayMode.LOOP_PINGPONG));
		animations.addRunning(
				new Animation(0.25f/2f, atlas.findRegions("brendan_run_north"), PlayMode.LOOP_PINGPONG), 
				new Animation(0.25f/2f, atlas.findRegions("brendan_run_south"), PlayMode.LOOP_PINGPONG), 
				new Animation(0.25f/2f, atlas.findRegions("brendan_run_east"), PlayMode.LOOP_PINGPONG), 
				new Animation(0.25f/2f, atlas.findRegions("brendan_run_west"), PlayMode.LOOP_PINGPONG));
		
		Array<World> loadedWorlds = app.getAssetManager().getAll(World.class, new Array<>());
		for (World w : loadedWorlds) {
			worlds.put(w.getName(), w);
		}
		world = worlds.get("littleroot_town");

		camera = new Camera();

		Random rand = new Random();
		int x = rand.nextInt((16-13)+1)+13;
		int y = rand.nextInt((7-3)+1)+3;
		player = new PlayerActor(world, x, y, animations, this, null);
		String message = "register " + player.getX() + " " + player.getY() + " ";
		udpClient.sendMessage(message);
		world.addActor(player);
		System.out.println(udpClient.getPort());

//		PlayerActor player2 = new PlayerActor(world, 13, 3, animations, this);
//		ChoiceDialogueNode node = new ChoiceDialogueNode("No", 0);
//		Dialogue player2Dialogue = new Dialogue();
//		node.addChoice("Yes", 0);
//		player2Dialogue.addNode(node);
//
//		player2Dialogue.addNode(node);
//
//		player2.setDialogue(player2Dialogue);
//		world.addActor(player2);

		initUI();

		multiplexer = new InputMultiplexer();
		
		playerController = new ActorMovementController(player , app);
		dialogueController = new DialogueController(dialogueBox, optionsBox);
		interactionController = new InteractionController(player, dialogueController);
		debugController = new OptionBoxController(debugBox);
		debugController.addAction(new Action() {
			@Override
			public void action() {
				renderTileInfo = !renderTileInfo;
			}
		}, "Toggle show coords");
		
		multiplexer.addProcessor(0, debugController);
		multiplexer.addProcessor(1, dialogueController);
		multiplexer.addProcessor(2, playerController);
		multiplexer.addProcessor(3, interactionController);
		worldRenderer = new WorldRenderer(getApp().getAssetManager(), world);
		queueRenderer = new EventQueueRenderer(app.getSkin(), eventQueue);
		tileInfoRenderer = new TileInfoRenderer(world, camera);
		nameRenderer = new NameRenderer(world, camera);
	}

	public static GameScreen getInstance(PokemonGame app){
		if(instance == null){
			instance = new GameScreen(app);
		}
		return instance;
	}

	public static GameScreen getInstance(){
		return instance;
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void pause() {
		
	}

	public void addNewPlayer(int x, int y, String playerID, String Address){
		PlayerActor player2 = new PlayerActor(world, x, y, animations, this, Address);
		LinearDialogueNode node = new LinearDialogueNode("Do you want to fight?", 0);
		Dialogue player2Dialogue = new Dialogue();
		player2Dialogue.addNode(node);
		player2.setDialogue(player2Dialogue);
		world.addActor(player2);
		players.put(playerID, player2);
		TempController tempController = new TempController(player2, this.getApp());
		tempControllers.put(playerID, tempController);
	}

	public void updatePlayer(int x, int y, int keyCodeUp, int keyCodeDown, String playerID){
		if (players.containsKey(playerID)){
			tempControllers.get(playerID).keyUp(keyCodeUp);
			tempControllers.get(playerID).keyDown(keyCodeDown);
		}
	}

	public void battle (String playerID){
		opponentAddress = playerID;
		isBattle = true;
		Tile target = player.getWorld().getMap().getTile(player.getX()+player.getFacing().getDX(), player.getY()+player.getFacing().getDY());
		if (target.getActor() != null) {
			Actor targetActor = target.getActor();

			if (targetActor.getDialogue() != null) {
				if (targetActor.refaceWithoutAnimation(DIRECTION.getOpposite(player.getFacing()))){
					optionsBox.setVisible(true);
					Dialogue dialogue = targetActor.getDialogue();
					dialogueController.startDialogue(dialogue);
				}
			}
		}
	}

	public void responseBattle(String playerID, String responseBattle){
		disableDialogue();
		System.out.println(responseBattle);
	}

	public void response(){
		if (isBattle){
			isBattle = false;
//			System.out.println(dialogueController.getAnswer());
			String messsage = "responsebattle " + opponentAddress + " " + dialogueController.getAnswer() + " ";
			udpClient.sendMessage(messsage);
		}

	}

	public void disableDialogue() {
		dialogueController.disableDialogue();
	}

	@Override
	public void update(float delta) {
		String message = "where " + player.getX() + " " + player.getY() + " " + playerController.getCurrentKeyUpCode() + " " + playerController.getCurrentKeyDownCode() + " ";
		udpClient.sendMessage(message);

		while (currentEvent == null || currentEvent.isFinished()) { // no active event
			if (eventQueue.peek() == null) { // no event queued up
				currentEvent = null;
				break;
			} else {					// event queued up
				currentEvent = eventQueue.poll();
				currentEvent.begin(this);
			}
		}
		
		if (currentEvent != null) {
			currentEvent.update(delta);
		}
			
		if (currentEvent == null) {
			playerController.update(delta);
			for (TempController tempController : tempControllers.values()){
				tempController.update(delta);
			}
		}
		
		dialogueController.update(delta);
		
		if (!dialogueBox.isVisible()) {
			camera.update(player.getWorldX()+0.5f, player.getWorldY()+0.5f);
			world.update(delta);
		}
		uiStage.act(delta);
	}

	@Override
	public void render(float delta) {

		gameViewport.apply();
		batch.begin();
		worldRenderer.render(batch, camera);
		queueRenderer.render(batch, currentEvent);
		if (renderTileInfo) {
			tileInfoRenderer.render(batch, Gdx.input.getX(), Gdx.input.getY());
		}
		nameRenderer.render(batch, player.getWorldX(), player.getWorldY());
		batch.end();
		
		uiStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		uiStage.getViewport().update(width/uiScale, height/uiScale, true);
		gameViewport.update(width, height);
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(multiplexer);
		if (currentEvent != null) {
			currentEvent.screenShow();
		}
	}
	
	private void initUI() {
		uiStage = new Stage(new ScreenViewport());
		uiStage.getViewport().update(Gdx.graphics.getWidth()/uiScale, Gdx.graphics.getHeight()/uiScale, true);
		//uiStage.setDebugAll(true);		// Uncomment to debug UI
		
		/*
		 * DIALOGUE SETUP
		 */
		dialogRoot = new Table();
		dialogRoot.setFillParent(true);
		uiStage.addActor(dialogRoot);
		
		dialogueBox = new DialogueBox(getApp().getSkin());
		dialogueBox.setVisible(false);
		
		optionsBox = new OptionBox(getApp().getSkin());
		optionsBox.addOption("Yes");
		optionsBox.addOption("No");
		optionsBox.setVisible(false);
		
		Table dialogTable = new Table();
		dialogTable.add(optionsBox)
						.expand()
						.align(Align.right)
						.space(8f)
						.row();
		dialogTable.add(dialogueBox)
						.expand()
						.align(Align.bottom)
						.space(8f)
						.row();
		
		
		dialogRoot.add(dialogTable).expand().align(Align.bottom);
		
		/*
		 * MENU SETUP
		 */
		menuRoot = new Table();
		menuRoot.setFillParent(true);
		uiStage.addActor(menuRoot);
		
		debugBox = new OptionBox(getApp().getSkin());
		debugBox.setVisible(false);
		
		Table menuTable = new Table();
		menuTable.add(debugBox).expand().align(Align.top | Align.left);
		
		menuRoot.add(menuTable).expand().fill();
	}
	
	public void changeWorld(World newWorld, int x, int y, DIRECTION face) {
		player.changeWorld(newWorld, x, y);
		this.world = newWorld;
		player.refaceWithoutAnimation(face);
		this.worldRenderer.setWorld(newWorld);
		this.camera.update(player.getWorldX()+0.5f, player.getWorldY()+0.5f);
	}

	@Override
	public void changeLocation(World newWorld, int x, int y, DIRECTION facing, Color color) {
		getApp().startTransition(
				this, 
				this, 
				new FadeOutTransition(0.8f, color, getApp().getTweenManager(), getApp().getAssetManager()), 
				new FadeInTransition(0.8f, color, getApp().getTweenManager(), getApp().getAssetManager()), 
				new Action() {
					@Override
					public void action() {
						changeWorld(newWorld, x, y, facing);
					}
				});
	}

	@Override
	public World getWorld(String worldName) {
		return worlds.get(worldName);
	}

	@Override
	public void queueEvent(CutsceneEvent event) {
		eventQueue.add(event);
	}
}
