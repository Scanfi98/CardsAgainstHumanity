package sample.networking;

import sample.Controller;
import sample.interfaccie.ControllerClient;
import sample.interfaccie.ControllerHost;
import sample.interfaccie.ControllerInterfaccie;
import sample.interfaccie.InterfacciaHost;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class ProtocolClient {

    private DataOutputStream outputStream;
    private String user;
    private ControllerInterfaccie controller;


    public ProtocolClient(DataOutputStream dataOutputStream, String user) {
        this.outputStream = dataOutputStream;
        this.user = user;
    }




    public void execute(String frame){
        System.out.println(user + "----------" + frame);
        String[] elements = frame.split("#");
        switch (elements[0]){
            case "STARTGAME":
                controller.firstRound();
                break;
            case "DELETEALL":
                controller.deleteAll();
                break;
            case "CHANGEBLACKCARD":
                if (controller.isGameStarted())
                controller.changeBlackText(elements[1]);
                break;
            case "POINTPLUS":
                if (controller.isGameStarted())
                controller.pointPlus(elements[1], elements[2]);
                break;
            case "ADDWHITECARD":
                if (controller.isGameStarted())
                controller.addWhiteCard(elements[1]);
                break;
            case "SELECTING":
                if (controller.isGameStarted())
                controller.startSelection(elements[1]);
                break;
            case "CARDCZAR":
                if (controller.isGameStarted())
                controller.setCardCzar(true);
                break;
            case "NEWROUND":
                if (controller.isGameStarted())
                controller.setNewRound();
                break;
            case "ADDPLAYER":
                controller.addPlayer(elements[1]);
                break;
            case "REMOVEPLAYER":
                controller.removePlayer(elements[1]);
            case "END":
                if(controller.isGameStarted() && elements[1].equals("VICTORY")){
                    controller.endGame(elements[2]);
                } else if(controller.isGameStarted()) {
                    controller.resetGame();
                }
                break;
            default:
                System.out.println("Unvalid frame");
                break;
        }
    }

    public void send(String toSend){
        try {
            outputStream.writeUTF(toSend);
        } catch (IOException e) {

        }
    }

    public String getUser() {
        return user;
    }

    public void setController(ControllerInterfaccie controller) {
        this.controller = controller;
    }
}
