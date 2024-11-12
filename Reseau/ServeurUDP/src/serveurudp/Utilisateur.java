/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package serveurudp;

import java.util.ArrayList;


/**
 *
 * @author kyllian cuevas
 */

public class Utilisateur {
    private String email,mdp; //l'email est le moyen d'identifier un utilisateur
    boolean connected=false;
    private ArrayList<Utilisateur> ListeAmis;
    private ArrayList<Utilisateur> ListeBloques;
    private ArrayList<Message> ListeMessage;
    private ArrayList <Utilisateur> ListeAttente;
    
    public Utilisateur(String mail, String password){ 
        email=mail;
        mdp=password;
        ListeAmis=new ArrayList();
        ListeBloques=new ArrayList();
        ListeMessage=new ArrayList();
        ListeAttente=new ArrayList();
    }
    
    public String getEmail(){ //retourner nom user
        return email;
    }
    
    public String getPassword(){ //retourner mdp
        return mdp;
    }
    
    public ArrayList<Utilisateur> getAmis(){//obtenir liste
        return ListeAmis;
    }
    
public void setAmis(Utilisateur u, boolean statut) {
    if (ListeAttente.contains(u) && !ListeBloques.contains(u) && ListeAmis.size() < 10) {
        if (statut) {
            ListeAttente.remove(u);
            ListeAmis.add(u);

            // Add the current user to the friend list of the other user
            u.ListeAmis.add(this);
        } else {
            ListeAttente.remove(u);
        }
    }
}
    
    public ArrayList<Utilisateur> getBloques(){//obtenir liste
        return ListeBloques;
    }
    
    public void setBloques(Utilisateur u,boolean statut){//bloquer user avec l'enlever de la liste ami etc
        //Si on veut bloquer, statut=true
        if(statut){
            if(ListeAmis.contains(u) && !ListeBloques.contains(u)){
                ListeAmis.remove(u);
                ListeBloques.add(u);
            }
        //Sinon, si statut=false, on debloque l utilisateur
        }else{
            if(ListeBloques.contains(u)){
                ListeBloques.remove(u);
            }
        }
    }
    
    public ArrayList <Utilisateur> getAttente(){ //liste d'attente des amis
        return ListeAttente;
    }
    
    public void setAttente(Utilisateur u){//ajt en attente
        if(!ListeAttente.contains(u) && !ListeBloques.contains(u)&& !ListeAmis.contains(u) && ListeAmis.size()<10 && ListeAttente.size()<10){
            ListeAttente.add(u);
        }
    }
    
    public void setConnexion(boolean state){//savoir si user en ligne ou non
        connected=state;
    }
    
    public boolean getConnexion(){//retourne état de connexion
        return connected;
    }
    
    public ArrayList<Message> getMessageList(){//retourne liste message
        return ListeMessage;
    }

    public void setMessage(Message message){ //ajoute message à la liste
        ListeMessage.add(message);
    }   

    public boolean isFriendWith(Utilisateur other){//vérif si l'user est ami avec verif dans la liste, retourn booleen pour savoir oui ou non
        if(ListeAmis.contains(other)){
            return true;
        }
        return false;
    }    
}