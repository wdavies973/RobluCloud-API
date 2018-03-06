package com.cpjd.commands;

import java.util.Scanner;

import com.cpjd.http.Request;
import com.cpjd.models.CloudTeam;
import com.cpjd.requests.AdminRequest;

public class Console {

	public static void main(String[] args) throws Exception {
		Scanner console = new Scanner(System.in);
		System.out.print("Enter server IP: ");
		String ip = console.nextLine();
		System.out.print("Enter your admin auth code: ");
		String auth = console.nextLine();
		
		while(true) {
			System.out.print("Enter a team name: ");
			String name = console.nextLine();
			System.out.print("Enter an email: ");
			String email = console.nextLine();

			Request r = new Request(ip);
			System.out.println("Is server online: "+r.ping());
			AdminRequest ar = new AdminRequest(r, auth);
			CloudTeam ct = ar.createTeam(name, email, "");
			if(ct == null) {
				System.out.println("Looks like a team with that name or number already exists.");
			} else {
				System.out.println("Team code: "+ct.getCode());	
			}
		}
	}
	
}
