package com.example.mymailapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class SendMail extends AsyncTask<Void, Void, Void>{
    private Context context;
    private ProgressDialog progressDialog;
    private int msgsRcvd = 0;

    public SendMail(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        progressDialog = ProgressDialog.show(context,"Retrieving Mail","Please wait...",false,false);
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            String filePath = context.getExternalFilesDir(null).getPath();
            File directory = new File(filePath);
            File[] files = directory.listFiles();

            Log.i("2", filePath + " contains " + Integer.toString(files.length) + " file(s)");

            //create properties field
            Properties properties = new Properties();

//            String host = "pop.gmail.com";// change accordingly
//            String mailStoreType = "pop3";

            String host = "imap.gmail.com";// change accordingly
            String mailStoreType = "imap";

            String username = "insecurepics13579@gmail.com";// change accordingly
            String password = "Xvsfwr@$35etdgcb";// change accordingly

//            properties.put("mail.pop3.host", host);
//            properties.put("mail.pop3.port", "995");
//            properties.put("mail.pop3.starttls.enable", "true");
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);

            //create the POP3 store object and connect with the pop server
//            Store store = emailSession.getStore("pop3s");
            Store store = emailSession.getStore("imaps");
            Log.i("1", "host---" + host);
            store.connect(host, username, password);

            Folder[] folders = store.getDefaultFolder().list("*");
            for (Folder folder : folders) {
                if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
                    Log.i("15",folder.getFullName() + ": " + folder.getMessageCount());
                }
            }

            if(files.length == 0)
            {
                Folder emailFolder = store.getFolder("SeenMail");
                emailFolder.open(Folder.READ_WRITE);

                // retrieve the messages from the folder in an array and print it
                Message[] messages = emailFolder.getMessages();
                Message[] delMessages = new Message[messages.length];
                int delmessagesIndex = 0;

                Log.i("1","messages.length---" + messages.length);
                msgsRcvd = messages.length;
                for (int i = 0, n = messages.length; i < n; i++) {
                    Message message = messages[i];
                    Log.i("1","---------------------------------");
                    Log.i("1","Email Number " + (i + 1));
                    Log.i("1","Subject: " + message.getSubject());
                    Log.i("1","From: " + message.getFrom()[0]);
                    Log.i("1","Text: " + message.getContent().toString());

                    if(message.getSubject().toLowerCase().contains("a1b2c3")) {
                        Log.i("1", "password exists, check for attachment");
                        MimeMultipart multiPart = (MimeMultipart) message.getContent();

                        for (int j = 0; j < multiPart.getCount(); j++) {
                            MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(j);
                            if (part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                                // this part is attachment
                                Log.i("1", "Attachment exists, time to save it");
                                // code to save attachment...
                                Log.i("3", context.getExternalFilesDir(null).toString());
                                part.saveFile(new File(context.getExternalFilesDir(null) + "/" + part.getFileName()));
                            }
                        }
                    }
                }
            }

            //create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);

            Folder DestFolder = store.getFolder("SeenMail");
            DestFolder.open( Folder.READ_WRITE );

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();
            Message[] delMessages = new Message[messages.length];
            int delmessagesIndex = 0;

            Log.i("1","messages.length---" + messages.length);
            msgsRcvd = messages.length;
            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
                Log.i("1","---------------------------------");
                Log.i("1","Email Number " + (i + 1));
                Log.i("1","Subject: " + message.getSubject());
                Log.i("1","From: " + message.getFrom()[0]);
                Log.i("1","Text: " + message.getContent().toString());

                if(message.getSubject().toLowerCase().contains("a1b2c3")) {
                    Log.i("1", "password exists, check for attachment");

                    MimeMultipart multiPart = (MimeMultipart) message.getContent();

                    for (int j = 0; j < multiPart.getCount(); j++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(j);
                        if (part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            // this part is attachment
                            Log.i("1", "Attachment exists, time to save it");
                            // code to save attachment...
                            Log.i("3", context.getExternalFilesDir(null).toString());
                            part.saveFile(new File(context.getExternalFilesDir(null) + "/" + part.getFileName()));
                        }
                    }
                }
            }

            emailFolder.copyMessages(messages, DestFolder);
            Flags deleted = new Flags(Flags.Flag.DELETED);
            emailFolder.setFlags(messages, deleted, true);
            emailFolder.expunge(); // or folder.close(true);

            Log.i("1",Integer.toString(messages.length) + " messages displayed");
            //close the store and folder objects
            emailFolder.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
//        progressDialog.dismiss();
        if(msgsRcvd == 1)
        {
            Toast.makeText(context, Integer.toString(msgsRcvd) + " Message Received", Toast.LENGTH_LONG).show();
        }else
        {
            Toast.makeText(context, Integer.toString(msgsRcvd) + " Messages Received", Toast.LENGTH_LONG).show();
        }
    }
}