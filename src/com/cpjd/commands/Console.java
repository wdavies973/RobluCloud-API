package com.cpjd.commands;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.cpjd.http.Request;
import com.cpjd.models.CloudTeam;
import com.cpjd.requests.AdminRequest;

/*
 * Functions:
 * -createTeam()
 * -cancelAllSubscriptions()
 * -deleteTeam()
 * -getTeam()
 */
public class Console implements ActionListener {

	private JFrame frame;
	private JButton createTeam, getTeam, deleteTeam, regenCode;
	private Request r;
	private String auth = "";
	private AdminRequest ar;
	
	public Console() {
		r = new Request("ec2-13-59-164-241.us-east-2.compute.amazonaws.com");
		ar = new AdminRequest(r, auth);
		
		Request.OUTPUT_RAW_RESPONSES = true;
		
		frame = new JFrame("Roblu Cloud Admin Controls");
		
		frame.setSize(400, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new FlowLayout());
		frame.setVisible(true);
		
		createTeam = new JButton("Create team");
		getTeam = new JButton("Get team");
		deleteTeam = new JButton("Delete team");
		regenCode = new JButton("Regenerate code");
		
		createTeam.addActionListener(this);
		getTeam.addActionListener(this);
		deleteTeam.addActionListener(this);
		regenCode.addActionListener(this);
		
		frame.add(createTeam);
		frame.add(getTeam);
		frame.add(deleteTeam);
		frame.add(regenCode);
	}
	
	
	public static void main(String[] args) throws Exception {
		new Console();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == createTeam) {
			String name = JOptionPane.showInputDialog("Enter team name: ");
			String ownerEmail = JOptionPane.showInputDialog("Enter owner email: ");
			String easy = RandomString.digits + "ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx0123456789";
			RandomString tickets = new RandomString(9, new SecureRandom(), easy);
			
			String code = tickets.nextString();
			
			CloudTeam team = ar.createTeam(name, ownerEmail, code);
			
			JOptionPane.showMessageDialog(null, team.toString());
			
			copyToClipboard("Registered owner email: "+ownerEmail+"\nRegistered team number: "+name+"\nTeam code: "+easy+"\nRecovery code: "+team.getSecret());
		}
		else if(event.getSource() == deleteTeam) {
			String name = JOptionPane.showInputDialog("Enter team name: ");
			String ownerEmail = JOptionPane.showInputDialog("Enter owner email: ");
			CloudTeam team = ar.deleteTeam(ownerEmail, name);
			JOptionPane.showMessageDialog(null, team.toString());
		}
		else if(event.getSource() == getTeam) {
			String ownerEmail = JOptionPane.showInputDialog("Enter owner email: ");
			CloudTeam team = ar.getTeamAsAdmin(ownerEmail);
			JOptionPane.showMessageDialog(null, team.toString());
			
			copyToClipboard("Registered owner email: "+ownerEmail+"\nRegistered team number: "+team.getOfficialTeamName()+"\nTeam code: "+team.getCode()+"\nRecovery code: "+team.getSecret());
		}
		else if(event.getSource() == regenCode) {
			String name = JOptionPane.showInputDialog("Enter team name: ");
			String ownerEmail = JOptionPane.showInputDialog("Enter owner email: ");
			String newcode = ar.regenerateCode(ownerEmail, name);
			JOptionPane.showMessageDialog(null, "New code: "+newcode);
			copyToClipboard(newcode);
		}
	}
	
	private void copyToClipboard(String text) {
		StringSelection stringSelection = new StringSelection(text);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}
	
}
