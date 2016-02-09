package com.example.gonzalo.pspchatterbot;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gonzalo.pspchatterbot.claseschat.ChatterBot;
import com.example.gonzalo.pspchatterbot.claseschat.ChatterBotFactory;
import com.example.gonzalo.pspchatterbot.claseschat.ChatterBotSession;
import com.example.gonzalo.pspchatterbot.claseschat.ChatterBotType;

import java.util.ArrayList;
import java.util.Locale;

public class Principal extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private android.widget.TextView tvConversacion;
    private android.widget.ScrollView scrollView;
    private android.widget.Button bt;
    private TextToSpeech tts;
    ChatterBot bot;
    private String yo="";
    public String conver="";
    private boolean speak;
    private TextView tvYo;
    private TextView tvBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        this.tvBot = (TextView) findViewById(R.id.tvBot);
        this.tvYo = (TextView) findViewById(R.id.tvYo);
        this.bt = (Button) findViewById(R.id.bt);
        this.scrollView = (ScrollView) findViewById(R.id.scrollView);
        this.tvConversacion = (TextView) findViewById(R.id.tvConversacion);
        setTitle("El Chatea-Bot");

        tvConversacion.setText("");
        tvBot.setText("");
        tvYo.setText("");
        init();
    }

    public void init(){
        Intent i = new Intent();
        i.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(i, 0);
    }

    public void conversar(View v){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "¿De qué quieres hablar?");
        i.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000);
        startActivityForResult(i, 1);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            speak = true;
//            tts= new TextToSpeech(this, this);
//            tts.setLanguage(new Locale("es", "ES"));

        } else {
            //no se puede reproducir
            Toast.makeText(Principal.this, "OMG!! no se puede reproducir", Toast.LENGTH_SHORT).show();
            speak= false;
        }
    }



    public class Tarea extends AsyncTask<String, Integer, String> {

        Tarea(String... p) {
        }

        @Override
        protected String doInBackground(String... params) {
            ChatterBotFactory botFactory = new ChatterBotFactory();
            try {
                bot = botFactory.create(ChatterBotType.CLEVERBOT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ChatterBotSession sessionBot = bot.createSession();
            String param = params[0];

            try {
                return sessionBot.think(param);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(speak==true){
                tvBot.setText("Bot: "+s);
                tts.setLanguage(new Locale("es", "ES"));
                tts.setPitch((float) 1.0);
                tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
                String r = "";
                r= r+ yo+ "\n"+tvBot.getText().toString()+"\n";
                tvConversacion.append(r);
            }
//            tvConversacion.append("\r\n"+ s);
//            tvConversacion.setText("");

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== 0) {
            if(resultCode== TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts= new TextToSpeech(Principal.this, Principal.this);
                tts.setLanguage(new Locale("es", "ES"));
            } else{
                Intent intent= new Intent();
                intent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(intent);
            }
        }

        if(requestCode== 1){
            if (resultCode == RESULT_OK ) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                conver=result.get(0);
                yo="Yo: "+conver;
                tvYo.setText(yo);

                Tarea t = new Tarea();
                t.execute(result.get(0));
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

}
