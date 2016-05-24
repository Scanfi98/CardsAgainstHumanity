package cardsagainsthumanity.networking;

import cardsagainsthumanity.game.Game;
import cardsagainsthumanity.interfaccie.InterfacciaConsole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class ProtocolServer {

    private ArrayList<Server.LinkedSocked> threadsGroup;
    private HashMap<String, String> carte;
    private Game game;
    private boolean selected = false;
    private Clock clock;
    private InterfacciaConsole console;

    public ProtocolServer(ArrayList<Server.LinkedSocked> threadsGroup) {
        this.threadsGroup = threadsGroup;
        this.carte = new HashMap<>();
        clock = new Clock();
}

    public void execute(String frame) {
        console.getControllerConsole().send(frame);
        String[] elements = frame.split("#");
        switch (elements[0]) {
            case "STARTGAME":
                game.start();
                this.clock = new Clock();
                clock.start();
                break;
            case "CARDWINNING":
                send("POINTPLUS#" + carte.get(elements[1].substring(0, elements[1].length() - 2)) + "#" + elements[1].substring(0, elements[1].length() - 2), PointerToSend.ALL, null);
                game.addPoint(carte.get(elements[1].substring(0, elements[1].length() - 2)));
                carte.clear();
                selected = true;
                break;
            case "CARDSELECTED":
                carte.put(elements[1].substring(0, elements[1].length() - 2), elements[2]);
                break;
            case "DISCONNECTED":
                send("REMOVEPLAYER#" + elements[1], PointerToSend.ALL, null);
                removeElementFromThreadGroup(elements[1]);
                game.removePlayer(elements[1]);
        }
    }

    public void send(String frame, PointerToSend group, String user) {
        Iterator it = threadsGroup.iterator();
        while (it.hasNext()) {
            Server.LinkedSocked t = (Server.LinkedSocked) it.next();
            try {
                if (group.equals(PointerToSend.ALL))
                    t.getWriter().writeUTF(frame);
                else if (user.equals(t.getUser())) t.getWriter().writeUTF(frame);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void reset(){
        carte.clear();
        send("DELETEALL", PointerToSend.ALL, null);

        Iterator it = threadsGroup.iterator();

        while (it.hasNext()){
            Server.LinkedSocked socked = (Server.LinkedSocked) it.next();
            sendName(socked.getUser());
            try {
                socked.getWriter().writeUTF("ADDPLAYER#" + socked.getUser());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendName(String user) {
        Iterator it = threadsGroup.iterator();
        Server.LinkedSocked t = null;
        boolean trovato = false;
        while (it.hasNext() && trovato == false) {
            t = (Server.LinkedSocked) it.next();
            if (t.getUser().equals(user)) {
                trovato = true;
            }
        }

        it = threadsGroup.iterator();
        while (it.hasNext() && trovato == true) {
            Server.LinkedSocked s = (Server.LinkedSocked) it.next();
            try {
                if (!s.getUser().equals(t.getUser())) t.getWriter().writeUTF("ADDPLAYER#" + s.getUser());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeElementFromThreadGroup(String user) {
        Iterator it = threadsGroup.iterator();
        boolean rimosso = false;
        while (it.hasNext() && !rimosso) {
            Server.LinkedSocked t = (Server.LinkedSocked) it.next();
            if (t.getUser().equals(user)) {
                t.interrupt();
                threadsGroup.remove(t);
                rimosso = true;
            }
        }
    }

    public void setConsole(InterfacciaConsole console) {
        this.console = console;
    }

    void setThreadsGroup(ArrayList<Server.LinkedSocked> threadsGroup) {
        this.threadsGroup = threadsGroup;
    }

    Game getGame() {
        return game;
    }

    public Clock getClock() {
        return clock;
    }

    public ArrayList<Server.LinkedSocked> getThreadsGroup() {
        return threadsGroup;
    }

    void setGame(Game game) {
        this.game = game;
    }

    public class Clock extends Thread {

        private boolean salta;
        private boolean choosing;

        Clock() {
            salta = false;
            choosing = true;
        }

        public void setSalta(boolean salta) {
            this.salta = salta;
        }

        public boolean isChoosing() {
            return choosing;
        }

        @Override
        public void run() {
            boolean interrupted = false;
            while (!interrupted){
                send("CARDCZAR", PointerToSend.USER, game.nextCardCzar());
                send("NEWROUND", PointerToSend.ALL, null);
                choosing = true;
                for (int i = 0; i < 60 && carte.size() < (threadsGroup.size() - 1) && !interrupted && !salta; i++) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        interrupted = true;
                    }
                }
                choosing = false;
                String all = "";
                Iterator it = carte.keySet().iterator();
                if(carte.size() > 0) {
                    while (it.hasNext()) {
                        all = all + it.next() + "!!";
                    }

                    send("SELECTING#" + all.substring(0, all.length() - 2), PointerToSend.ALL, null);

                for (int i = 0; i < 60 && !selected && !interrupted && !salta; i++) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        interrupted = true;
                    }
                }

                for (int i = 0; i < 8 && !interrupted && selected && !salta; i++) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        interrupted = true;
                    }
                }
                }
                game.refillWhiteCard();
                game.sendBlackCard();
                selected = false;
                salta = false;
            }


        }
    }
}
