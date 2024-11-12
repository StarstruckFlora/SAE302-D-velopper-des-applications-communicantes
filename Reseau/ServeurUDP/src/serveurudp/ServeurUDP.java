package serveurudp;
//On importe les differentes librairies qui nous seront utiles
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class ServeurUDP {
    //On cree une liste d'utilisateur qui permettra au serveur de savoir
    //Si un utilisateur est valide ou non
    static ArrayList<Utilisateur> ListeUtilisateurs;
    //On definit le port d'ecoute du serveur
    final private static int port = 6010 ;

    //la methode suivante permet de verifier si un utilisateur existe dans la liste
    //des utilisateurs du serveur, on donne un nom d'utilisateur de type string en argument
    //On on obtient le meme nom d'utilisateur s'il existe et on obtient none s'il n'existe pas
    public static String getUserByMail(String mail){
        int compteur=0;
        boolean found=false;
        String ID;

        while(compteur<ListeUtilisateurs.size() || found==true){
                if(ListeUtilisateurs.get(compteur).getEmail().equals(mail)){
                    ID=ListeUtilisateurs.get(compteur).getEmail();
                    found=true;
                    return ID;
                }else{
                    compteur+=1;
                }
        }
        return "None";
    }

    //Cette methode permet egalement de verifier la presence d'un utilisateur dans
    //la liste des utilisateurs. Mais son reel but est de retourner l'objet utilisateur
    //correspondant a un nom donne pour pouvoir y appliquer des methodes
    //Dans le cas ou l'utilisateur n'existe pas, on retourne un utilisateur invalide
    //qui ne sera pas utilise par le serveur et qui retournera un message d'erreur
    public static Utilisateur returnUser(String mail){
        int compteur=0;
        boolean found=false;
        Utilisateur ID;
        //tant que le compteur ne depasse pas la taille de la liste ou qu'on ne
        //trouve pas l'utilsisateur dans la liste, on parcourt chaque donnee
        while(compteur<ListeUtilisateurs.size() || found==true){
            System.out.println(compteur);
                //Si le nom d'utilisateur de l'element a la position compteur de la liste
                //correspond au mail donnee, on change la valeu rdu booleen found en vrai
                //et on recupere l'object correspondant
                if(ListeUtilisateurs.get(compteur).getEmail().equals(mail)){
                    ID=ListeUtilisateurs.get(compteur);
                    found=true;
                    return ID;
                }else{
                    compteur+=1;
                }
        }
        //On retourne un utilisateur appelle utilisateurintrouvable dont le mot de
        //passe est None si on ne trouve pas l'utilisateur donnne en argument dans la liste
        return new Utilisateur("UtilisateurIntrouvable","None");
    }

    public static void main(String args[]) throws SocketException, IOException, InterruptedException {
        ListeUtilisateurs=new ArrayList(); //création de la liste des utilisateurs
        ListeUtilisateurs.add(new Utilisateur("nicolas","rabergeau")); //on ajoute certains nom dans la liste (remplire la base de données)
        ListeUtilisateurs.add(new Utilisateur("xavier","pout"));
        ListeUtilisateurs.add(new Utilisateur("etienne","paquelet"));
        //System.out.println("Les utilisateurs sont dans la base de données");
        System.out.println("Bon fonctionnement du serveur :\n");

        byte [] buffer = new byte [1024] ; //taille d'un paquet
        String s ;
        DatagramSocket socket = new DatagramSocket(port) ;

        while(true){
            DatagramPacket packet =new DatagramPacket(buffer, buffer.length);// définir l'endroit où iront les packets : emplacement mémoire, 1024
            socket.receive(packet) ;//Reçoit les données d'un Socket lié dans une mémoire tampon de réception
            s = new String(buffer, 0, 0, packet.getLength()) ;// ???????
            System.out.println("Paquet recu ==>  "+s) ; //afficher creation
            String t[]=s.split(",");//on sépare dans tableau la requete sous la forme -> type | identifiant | mot de passe
            boolean connexion=false;//initialisation de deux booléens
            boolean creation=false;


            //////////////////////////////////////
            //////////////////////////////////////
            //            CREATION              //
            //////////////////////////////////////
            //////////////////////////////////////

            if(t[0].equals("creation")){ //on va voir quel type -> creation
                boolean exists=false;
                //System.out.println(ListeUtilisateurs.size());
                if(ListeUtilisateurs.size()<10){
                    System.out.println("Type de requete ==>  INSCRIPTION");
                    for(int i=0;i<ListeUtilisateurs.size();i++){//parcours va vérifier dans tous les éléments de la liste ...
                        if(ListeUtilisateurs.get(i).getEmail().equals(t[1])){ //...si l'identifiant = à celui dans la requête on entre dans la condition => on ajoute pas l'identifiant si il est déja dans la BD
                            System.out.println("Erreur, votre identifiant est déjà utilisé");
                            exists=true;//permet de ne pas rentrer dans la prochaine conditions
                        }
                    }
                    if (exists==false){ //quand on est sur que l'utilisateur n'existe pas
                        System.out.println("L'utilisateur peut etre ajoute a la base de donnees");
                        ListeUtilisateurs.add(new Utilisateur(t[1],t[2])); // ajoute l'utilisateur dans la base de données (tableau)
                        creation=true; //on peut passer à l'creation car la valeur était fausse avant
                    }
                }
                if(creation){ //on passe à la confirmation de l'creation au client
                    String msg2=t[1]+" , l creation est reussie !"; //le message à renvoyer au client est celui là
                    int msglen2 = msg2.length() ; //on met le nbr de caractères du message au client dans une varible (25)
                    byte [] message2 = new byte [1024] ; //?????????
                    msg2.getBytes(0, msglen2, message2, 0) ; //?????????
                    DatagramPacket packetResp =new DatagramPacket(message2, msglen2, packet.getAddress(), packet.getPort()) ; //encapsulation du packet avec l'adresse dst que l'ancienne addr src, pareil pour le port
                    DatagramSocket socketResp = new DatagramSocket() ;//?????????
                    socketResp.send(packetResp) ;//envoie par le socket (port voulu par la machine)
                    System.out.println("--> Le creation de "+t[1]+" est validee.\n\n");//met sur le serveur que le creation est bon (validation console)
                }
            }


            //////////////////////////////////////
            //////////////////////////////////////
            //            Connexion             //
            //////////////////////////////////////
            //////////////////////////////////////
            if(t[0].equals("connexion")){
                boolean founded=false;
                //System.out.println(ListeUtilisateurs.size());
                System.out.println("Type de requete ==>  CONNEXION");
                //On parcourt la liste des utilisateurs, et on regarde si le mail donne
                //par le client correspond a un utilisateur existant
                for(int i=0;i<ListeUtilisateurs.size();i++){
                    //S il correspond, on recupere le mail et le mot de passe de l'utilisateur
                    //on va utiliser le mot de passe de l utilisateur et on va le comparer
                    //avec le mot de passe fourni par le client pour verifier s il est correct ou non

                    if(ListeUtilisateurs.get(i).getEmail().equals(t[1])){
                        founded=true;
                        Utilisateur user=ListeUtilisateurs.get(i);
                        String mail=ListeUtilisateurs.get(i).getEmail();
                        String password=ListeUtilisateurs.get(i).getPassword();
                        System.out.println("L'utilisateur "+mail+" est deja dans la base de donnees");
                        System.out.println("Voici l etat de connexion "+user.getConnexion());
                        if(password.equals(t[2]) && user.getConnexion()==false){
                            user.setConnexion(true);
                            System.out.println("Connexion de "+t[1]+" reussie");
                            connexion=true;
                            founded=true;

                        }else{
                            founded=true;
                            System.out.println("Connexion refusee : compte encore connecte ou mauvais mot de passe");
                            String msg2="Erreur, connexion refusee : compte encore connecte ou mauvais mot de passe";
                            int msglen2 = msg2.length() ;
                            byte [] message2 = new byte [msglen2] ;
                            msg2.getBytes(0, msglen2, message2, 0) ;
                            DatagramPacket packetResp =new DatagramPacket(message2, msglen2, packet.getAddress(), packet.getPort()) ;
                            DatagramSocket socketResp = new DatagramSocket() ;
                            socketResp.send(packetResp) ;
                        }
                    }
                }
                if(founded==false){
                    System.out.println("L'utilisateur n'exite pas\n\n");
                    String msg2="Erreur, veuillez-vous inscrire";
                    int msglen2 = msg2.length() ;
                    byte [] message2 = new byte [msglen2] ;
                    msg2.getBytes(0, msglen2, message2, 0) ;
                    DatagramPacket packetResp =new DatagramPacket(message2, msglen2, packet.getAddress(), packet.getPort()) ;
                    DatagramSocket socketResp = new DatagramSocket() ;
                    socketResp.send(packetResp) ;
                }
                if(connexion){
                    String msg2=t[1]+" , votre connexion a ete effectuee avec succes";
                    int msglen2 = msg2.length() ;
                    byte [] message2 = new byte [msglen2] ;
                    msg2.getBytes(0, msglen2, message2, 0) ;
                    DatagramPacket packetResp =new DatagramPacket(message2, msglen2, packet.getAddress(), packet.getPort()) ;
                    DatagramSocket socketResp = new DatagramSocket() ;
                    socketResp.send(packetResp) ;
                    System.out.println("--> La connexion de "+t[1]+" est validee.\n\n");
                }
            }


        //////////////////////////////////////
        //       FORME REQUETE MESSAGE      //
        //  message,mail1,mail2,sujet,data  //
        //////////////////////////////////////
        //On regarde si la requete recue est un message
            if(t[0].equals("message")){
              //On recupere les deux noms d'utilisateurs donnees et on regarde s'ils correspondent
              //a des utilisateurs deja existants sur le serveur
                System.out.println(getUserByMail(t[1]));
                System.out.println(getUserByMail(t[2]));

                if(!(getUserByMail(t[1]).equals("None")) && !(getUserByMail(t[2]).equals("None"))){

                    System.out.println(t[3]+" "+t[4]);
                    //Si c'est le cas, on recupere les objets utilisateurs correspondants
                    Utilisateur usernumber1=returnUser(t[1]);
                    Utilisateur usernumber2=returnUser(t[2]);

                    /*On verifie les differentes contraintes qui empechent l'envoi d'un message
                    Si es utilisateurs sont amis, et s'ils existent bien dans la base de donnee
                    on envoie le message qui sera stocke dans les listes des messages des deux utilisateurs
                  */
                    if(usernumber1.isFriendWith(usernumber2) && !(usernumber1.getEmail().equals("UtilisateurIntrouvable")) && !usernumber2.getEmail().equals("UtilisateurIntrouvable")){

                        Message message=new Message(usernumber1,t[3],usernumber2);
                        System.out.println(message);
                        message.Ecrire_Message(t[3], t[4]);
                        usernumber1.setMessage(message);
                        usernumber2.setMessage(message);
                        System.out.println(usernumber1.getMessageList());

                        try {
                            String msg2="Client : votre message a bien ete envoye";
                            System.out.println(msg2);
                            int msglen2 = msg2.length() ;
                            byte [] message2 = new byte [1024] ;
                            msg2.getBytes(0, msglen2, message2, 0) ;
                            DatagramPacket packetResp =new DatagramPacket(message2, msglen2, packet.getAddress(), packet.getPort()) ;
                            DatagramSocket socketResp = new DatagramSocket() ;
                            socketResp.send(packetResp) ;
                        } catch (IOException e) {
                            System.out.println("CPT");
                        }
                    }else{

                        try {
                            String msg2="Le message ne peut etre envoye car "+t[1]+" ou "+t[2]+" exitent pas";
                            int msglen2 = msg2.length() ;
                            byte [] message2 = new byte [1024] ;
                            msg2.getBytes(0, msglen2, message2, 0) ;
                            DatagramPacket packetResp =new DatagramPacket(message2, msglen2, packet.getAddress(), packet.getPort()) ;
                            DatagramSocket socketResp = new DatagramSocket() ;
                            socketResp.send(packetResp) ;
                        } catch (IOException e) {
                            System.out.println("CPT");
                        }
                    }
                }else{
                    try {
                        String msg2="Le message ne peut etre envoye car les users renseignes ne sont pas bons";

                        int msglen2 = msg2.length() ;
                        byte [] message2 = new byte [1024] ;
                        msg2.getBytes(0, msglen2, message2, 0) ;
                        DatagramPacket packetResp =new DatagramPacket(message2, msglen2, packet.getAddress(), packet.getPort()) ;
                        DatagramSocket socketResp = new DatagramSocket() ;
                        socketResp.send(packetResp) ;
                    } catch (IOException e) {
                        System.out.println("CPT");
                    }
                }
            }

    //////////////////////////////////////
    //////////////////////////////////////
    //  FORME REQUETE LECTURE MESSAGE   //
    //       lecture,user,dest          //
    //////////////////////////////////////
    //////////////////////////////////////
    //la requete lecture permet a un utilisateur de lire les messages qu'il a
    //echange avec un autre utilisateur
            if(t[0].equals("lecture")){
                //On verifie si les deux utilisateurs existent
                if(!getUserByMail(t[1]).equals("None") && !getUserByMail(t[2]).equals("None")){
                    Utilisateur user1=returnUser(t[1]);
                    Utilisateur user2=returnUser(t[2]);
                    System.out.println(user1.getMessageList().size()+ " taille de la liste");
                    String msg2="messagesfrom,";
                    //On parcourt tout les messsages de l'utilsiateur 1
                    //Si on trouve que le destinateur ou l'auteur est l'utilisateur 2
                    //alors on l'ajoute a la liste des messages retournes
                    for(int i=0;i<user1.getMessageList().size();i++){
                        if(user1.getMessageList().get(i).GetSender().equals(user2) || user1.getMessageList().get(i).GetReceiver().equals(user2)){

                            msg2+=user1.getMessageList().get(i).GetSender().getEmail()+"/"+user1.getMessageList().get(i).getContent()+",";
                        }
                    }
                    try {

                        System.out.println("MESSAGE READ OK");
                        int msglen2 = msg2.length() ;
                        byte [] message2 = new byte [1024] ;
                        msg2.getBytes(0, msglen2, message2, 0) ;
                        DatagramPacket packetResp =new DatagramPacket(message2, msglen2, packet.getAddress(), packet.getPort()) ;
                        DatagramSocket socketResp = new DatagramSocket() ;
                        socketResp.send(packetResp) ;
                    } catch (IOException e) {
                        System.out.println("CPT");
                    }
                } else {
                    System.out.println("pas bon");
                }
            }

            //////////////////////////////////////
            //////////////////////////////////////
            //        FORME REQUETE MAJ         //
            //          actualisation           //
            //       des differentes listes     //
            //       forme : update@user        //
            //////////////////////////////////////
            //////////////////////////////////////

            //la requete update permet a un utilisateur de demander regulierement
            //sa liste d'amis, d'utilisateurs bloques et de demandes d'amis
            if(t[0].equals("update")){
              //si la requete est bien update et si on a un utilisateur en argument
                System.out.println("update");
                if(!getUserByMail(t[1]).equals("None")){
                    Utilisateur user1=returnUser(t[1]);
                    System.out.println(user1.getMessageList().size()+ " taille de la liste des messages");
                    //on cree des listes qui vont contenir les listes des amis des utilisateurs sous forme
                    //d'objet de type utilisateur
                    ArrayList<Utilisateur> amis=user1.getAmis();
                    ArrayList<Utilisateur> bloques=user1.getBloques();
                    ArrayList<Utilisateur> attente=user1.getAttente();

                    //on cree egalement la meme version de ces listes mais avec les noms des utilisateurs
                    //sous forme de chaine de carateres pour pouvoir exploiter les donness sur le client
                    ArrayList<String> amis2;
                    ArrayList<String> bloques2;
                    ArrayList<String> attente2;
                    amis2=new ArrayList();
                    bloques2=new ArrayList();
                    attente2=new ArrayList();


                    //On va parcourir chaque liste d'objet utilisateur et convertir les donnees en string
                    //avec la methode getemail qui retourne le nom de l'utilsateur
                    for (int i=0; i<amis.size();i++){
                        amis2.add(amis.get(i).getEmail());
                    }
                    for (int i=0; i<bloques.size();i++){
                        bloques2.add(bloques.get(i).getEmail());
                    }
                    for (int i=0; i<attente.size();i++){
                        attente2.add(attente.get(i).getEmail());
                    }
                    try {
                      //On retourne ensuite ces trois listes dans un socket
                        String msg2="voici les amis,"+amis2+",voici les bloques,"+bloques2+",voici les attentes,"+attente2;
                        System.out.println("UPDATE OK");
                        System.out.println(msg2+"<- MSG2");
                        int msglen2 = msg2.length() ;
                        byte [] message2 = new byte [1024] ;
                        msg2.getBytes(0, msglen2, message2, 0) ;
                        DatagramPacket packetResp =new DatagramPacket(message2, msglen2, packet.getAddress(), packet.getPort()) ;
                        DatagramSocket socketResp = new DatagramSocket() ;
                        socketResp.send(packetResp) ;
                    } catch (IOException e) {
                        System.out.println("CPT");
                    }
                } else {
                    System.out.println("pas bon");
                }
            }

            ///////////////////////////////////////////////
            ///////////////////////////////////////////////
            //            FORME REQUETE AMIS             //
            //ami,demande/accept/refus/bloque,user1,user2//
            ///////////////////////////////////////////////
            ///////////////////////////////////////////////

            //la requete ami permet de gerer toutes les relations a la fois
            //on peut envoyer une demande d'amis, l'accepter, la refuser
            //on peut aussi bloquer l'utilisateur 2
            if(t[0].equals("ami")){

                if(t[1].equals("demande")){
                  //dans le cas d'une demande d'amis, on definit que c'est l'utilisateur 1 qui envoie
                  //une demande a l'utilsiateur2. On modifie donc la liste de l'utilisateur 2
                    if(!getUserByMail(t[2]).equals("None") && !getUserByMail(t[3]).equals("None")){
                        Utilisateur user1=returnUser(t[2]);
                        Utilisateur user2=returnUser(t[3]);
                        user2.setAttente(user1);
                        System.out.println(user2.getAttente()+"< attente u2");
                        System.out.println(user1.getAttente()+"< attente u1");

                        try {
                            String msg2="Demande envoyee";

                            int msglen2 = msg2.length() ;
                            byte [] message2 = new byte [1024] ;
                            msg2.getBytes(0, msglen2, message2, 0) ;
                            DatagramPacket packetResp =new DatagramPacket(message2, msglen2, packet.getAddress(), packet.getPort()) ;
                            DatagramSocket socketResp = new DatagramSocket() ;
                            socketResp.send(packetResp) ;
                        } catch (IOException e) {
                            System.out.println("CPT");
                        }
                    } else {

                        String msg2="Peut pas etre ajoute";

                        int msglen2 = msg2.length() ;
                        byte [] message2 = new byte [1024] ;
                        msg2.getBytes(0, msglen2, message2, 0) ;
                        DatagramPacket packetResp =new DatagramPacket(message2, msglen2, packet.getAddress(), packet.getPort()) ;
                        DatagramSocket socketResp = new DatagramSocket() ;
                        socketResp.send(packetResp) ;
                    }

                }if(t[1].equals("accept")){
                  //dans le cas d'une acceptation de demande d'amis, on regarde si les deux utilisateurs existent bien
                    if(!getUserByMail(t[2]).equals("None") && !getUserByMail(t[3]).equals("None")){
                      //On recupere les objets correspondants aux noms donnees en argument
                        Utilisateur user1=returnUser(t[2]);
                        System.out.println(returnUser(t[2]).getEmail());
                        Utilisateur user2=returnUser(t[3]);
                        System.out.println(returnUser(t[3]).getEmail());

                        //Pour pouvoir ajouter les utilisateurs en ami dans les deux listes
                        //On doit imperativement ajouter une demande d'amis des deux cotes
                        //la condition permettant de valider une demande d'amis necessite qu'ne
                        //demande soit deja presente dans la liste d'attente
                        user1.setAttente(user2);

                        System.out.println(user1.getAttente()+ " <- Liste Attente de user1");
                        System.out.println(user2.getAttente()+ " <- Liste Attente de user2");

                        //On accepte la demande d'amis grace au booleen true, on l'aurait refuse
                        //si on avait envoye false
                        user1.setAmis(user2,true);
                        user2.setAmis(user1,true);

                        System.out.println(user1.getAttente());
                        System.out.println(user2.getAttente());
                        System.out.println(user1.getAmis());
                        System.out.println(user2.getAmis());

                        try {
                            String msg2="Demande acceptee";
                            System.out.println("AMIS ACCEPT");
                            System.out.println(user1.getAmis()+ " <- Liste Amis de user1");
                            System.out.println(user2.getAmis()+ " <- Liste Amis de user2");
                            int msglen2 = msg2.length() ;
                            byte [] message2 = new byte [1024] ;
                            msg2.getBytes(0, msglen2, message2, 0) ;
                            DatagramPacket packetResp =new DatagramPacket(message2, msglen2, packet.getAddress(), packet.getPort()) ;
                            DatagramSocket socketResp = new DatagramSocket() ;
                            socketResp.send(packetResp) ;
                        } catch (IOException e) {
                            System.out.println("CPT");
                        }
                    } else {
                        System.out.println("pas bon");
                    }
                }if(t[1].equals("refus")){
                    if(!getUserByMail(t[2]).equals("None") && !getUserByMail(t[3]).equals("None")){

                        Utilisateur user1=returnUser(t[1]);
                        Utilisateur user2=returnUser(t[2]);

                        //On refuse la demande d'amis des deux cotes grace au booleen false
                        user2.setAmis(user1,false);
                        user1.setAmis(user2,false);

                        try {
                            String msg2="Demande refusee";
                            System.out.println("AMIS REFUS");
                            int msglen2 = msg2.length() ;
                            byte [] message2 = new byte [1024] ;
                            msg2.getBytes(0, msglen2, message2, 0) ;
                            DatagramPacket packetResp =new DatagramPacket(message2, msglen2, packet.getAddress(), packet.getPort()) ;
                            DatagramSocket socketResp = new DatagramSocket() ;
                            socketResp.send(packetResp) ;
                        } catch (IOException e) {
                            System.out.println("CPT");
                        }
                    } else {
                        System.out.println("pas bon");
                    }
                }
                if(t[1].equals("bloque")){

                    if(!getUserByMail(t[2]).equals("None") && !getUserByMail(t[3]).equals("None")){

                        Utilisateur user1=returnUser(t[1]);
                        Utilisateur user2=returnUser(t[2]);

                      //dans le cas d'un blocage, la methode setbloques va regarder
                      //si l'utilisateur 1est deja dans la liste d'amis de l'utilisateur 2
                      //puis on va bloque l'amis avec le booleen true, false aurait permis de le
                      //debloquer
                      //on retire donc l'utilisateur 2 de la liste d'amis de l'utilsiateur 1
                        user2.setBloques(user1,true);
                        user1.setAmis(user2, false);
                        try {
                            String msg2="Utilisateur bloque";
                            System.out.println("AMIS BLOQUE");
                            int msglen2 = msg2.length() ;
                            byte [] message2 = new byte [1024] ;
                            msg2.getBytes(0, msglen2, message2, 0) ;
                            DatagramPacket packetResp =new DatagramPacket(message2, msglen2, packet.getAddress(), packet.getPort()) ;
                            DatagramSocket socketResp = new DatagramSocket() ;
                            socketResp.send(packetResp) ;
                        } catch (IOException e) {
                            System.out.println("CPT");
                        }
                    } else {
                        System.out.println("pas bon");
                    }
                }

                //la methode deconnexion permet de changer la valeur du booleen
                //stocke dans la classe utilisateur, cela permet de deconnecter une session
                //active et de permettre une connexion sur un autre client
            }if(t[0].equals("deconnexion")){
                System.out.println("decoonexion");
                String var=t[1];
                Utilisateur user=returnUser(var);
                user.setConnexion(false);
                String msg2="stop user";
                System.out.println("stop user");
                int msglen2 = msg2.length() ;
                byte [] message2 = new byte [1024] ;
                msg2.getBytes(0, msglen2, message2, 0) ;
                DatagramPacket packetResp =new DatagramPacket(message2, msglen2, packet.getAddress(), packet.getPort()) ;
                DatagramSocket socketResp = new DatagramSocket() ;
                socketResp.send(packetResp) ;
            }
        }
    }
}