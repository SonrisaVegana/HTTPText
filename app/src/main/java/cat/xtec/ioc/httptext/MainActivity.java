package cat.xtec.ioc.httptext;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    // Textview on es mostrarà la informació
    TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.textView1);

        // Executem l'AsyncTask amb la URL.
        new DescarregaText().execute("http://www.sonrisavegana.com");

    }

    // AsyncTask que descarrega text de la xarxa
    private class DescarregaText extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // Descarreguem el text passat per argument
            return descarregaText(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            // Quan ha acabat la tasca, posem el text al TextView
            tv.setText(s);
        }
    }

    private String descarregaText(String URL) {
        int BUFFER_SIZE = 2000;            //Mida del buffer de text
        BufferedInputStream in;    //Flux de dades de lectura

        try {
            //Obrim la connexió
            in = ObreConnexioHTTP(URL);
        } catch (IOException e) {
            //Error
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return "";
        }

        //Obtenim un flux de caracters
        InputStreamReader isr = new InputStreamReader(in);

        char[] inputBuffer = new char[BUFFER_SIZE];        //Buffer de caràcters
        int caractersLlegits;                            //Caràcters llegits
        String stringResultat = "";                        //String resultat


        try {
            //Mentre s'hagin llegit caràcters
            while ((caractersLlegits = isr.read(inputBuffer)) > 0) {
                //Convertim els caràcters a String
                String stringLlegit = String.copyValueOf(inputBuffer, 0, caractersLlegits);

                //Afegim els caracters llegits al resultat
                stringResultat += stringLlegit;

            }
            //Tanquem la connexió
            in.close();
        } catch (IOException e) {
            //Excepció
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return "";
        }

        //Retornem el resultat
        return stringResultat;

    }


    private BufferedInputStream ObreConnexioHTTP(String adrecaURL) throws IOException {
        BufferedInputStream in = null;
        int resposta;

        URL url = new URL(adrecaURL);
        URLConnection connexio = url.openConnection();

        if (!(connexio instanceof HttpURLConnection))
            throw new IOException("No connexió HTTP");

        try {
            HttpURLConnection httpConn = (HttpURLConnection) connexio;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            resposta = httpConn.getResponseCode();
            if (resposta == HttpURLConnection.HTTP_OK) {
                in = new BufferedInputStream(httpConn.getInputStream());
            }
        } catch (Exception ex) {
            throw new IOException("Error connectant");
        }

        return in;
    }
}
