package main

import (
	"fmt"
	"net"
	"os"
	"strings"
	"time"
)

type ClientInfo struct {
	IP   string
	Port int
	X   string
	Y   string
}

var clients = make(map[string]ClientInfo)

func main() {
	service := ":1200"
	udpAddr, err := net.ResolveUDPAddr("udp4", service)
	checkError(err)
	conn, err := net.ListenUDP("udp", udpAddr)
	checkError(err)
	for {
		handleClient(conn)
	}
}

func handleClient(conn *net.UDPConn) {
	var buf [512]byte
	n, addr, err := conn.ReadFromUDP(buf[0:])
	if err != nil {
		return
	}
	message := string(buf[:n])
	// fmt.Println("Received: ", message)
	// fmt.Println("Client IP: ", addr.IP.String())
	// fmt.Println("Client Port: ", addr.Port)

	parts := strings.Split(message, " ")

	ClientIP := addr.IP.String()
	ClientPort := addr.Port

	if parts[0] == "where" {
		playerX := parts[1]
		playerY := parts[2]
		keyCodeUp := parts[3]
		keyCodeDown := parts[4]
		clients[addr.String()] = ClientInfo{IP: ClientIP, Port: ClientPort, X: playerX, Y: playerY}
		for clientAddr := range clients {
			if clientAddr != addr.String() {
				message := "where " + playerX + " " + playerY + " " + keyCodeUp + " " + keyCodeDown + " " + clientAddr
				udpClientAddr, _ := net.ResolveUDPAddr("udp", clientAddr)
				_, err := conn.WriteToUDP([]byte(message), udpClientAddr)
				if err != nil {
					fmt.Println("Error: ", err)
				}
			}
		
		}

	} else if parts[0] == "register" {
		
		playerX := parts[1]
		playerY := parts[2]
		fmt.Println("Player X: ", playerX)
		fmt.Println("Player Y: ", playerY)
		

		clients[addr.String()] = ClientInfo{IP: ClientIP, Port: ClientPort, X: playerX, Y: playerY}

		time.Sleep(2 * time.Second)

		for clientAddr := range clients {
			if clientAddr != addr.String() {
				message := "newplayer " + playerX + " " + playerY + " " + clientAddr
				udpClientAddr, _ := net.ResolveUDPAddr("udp", clientAddr)
				_, err := conn.WriteToUDP([]byte(message), udpClientAddr)
				if err != nil {
					fmt.Println("Error: ", err)
				}
			}
		}

		for clientAddr := range clients {
			if clientAddr != addr.String() {
				message := "newplayer " + clients[clientAddr].X + " " + clients[clientAddr].Y + " " + addr.String()
				udpClientAddr, _ := net.ResolveUDPAddr("udp", addr.String())
				_, err := conn.WriteToUDP([]byte(message), udpClientAddr)
				if err != nil {
					fmt.Println("Error: ", err)
				}
			}
		}
	
	}
}

func checkError(err error) {
	if err != nil {
		_, err := fmt.Fprintf(os.Stderr, "Fatal error ", err.Error())
		if err != nil {
			return
		}
		os.Exit(1)
	}
}