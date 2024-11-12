/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package serveurudp;

/**
 *
 * @author thomas mirbey
 */

public class Message {
    private String motif,subject,maintext;
    private Utilisateur userSource,userDest;
    private boolean seen,modified;
    
    public Message(Utilisateur UserSource,String Motif,Utilisateur UserDest){//constructeur
        userSource=UserSource;
        userDest=UserDest;
        motif=Motif;
    }
    
    public String Ecrire_Message(String Sujet,String Corps){//forme message a ecrire
        maintext=Corps;
        subject=Sujet;
        return "message,"+userSource.getEmail()+","+userDest.getEmail()+","+Sujet+","+Corps;
    }

    //Utile pour remplir la liste de messages.
    public Utilisateur GetSender(){//retourne utilisateur qui a envoyé le message
        return userSource;
    }
    //plus de sens
    public String GetSenderMail(){
        return userSource.getEmail();
    }
    
    public String getContent(){//retourne contenu du message
        return maintext;
    }
    
    public String getSubject(){//retourne sujet message (pas utilise du coup)
        return subject;
    }
    
    public Utilisateur GetReceiver(){//utilisateur a qui va le message
        return userDest;
    }
    
    public String GetReceiverMail(){
        return userDest.getEmail();
    }  
}