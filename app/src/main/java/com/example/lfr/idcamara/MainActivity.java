package com.example.lfr.idcamara;
import java.io.File;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final int CAMARA = 1;
    static final int GALERIA = 2;
    Intent intentCamera;
    Intent intentGaleria;
    Bitmap bmp;
    ImageView visor;
    TextView titulofoto;
    String tituloValue="";
    String mCurrentPhotoPath;
    SimpleDateFormat fecha=new SimpleDateFormat("yyyyMMdd_HHmmss");
    String fechafoto;
    String nombrefoto;
    File fototemp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        visor = (ImageView) this.findViewById(R.id.ImgVisor);
        titulofoto = (TextView) this.findViewById(R.id.tituloFoto);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //bmp=BitmapFactory.decodeFile(tituloValue);
                //String condicion=bmp.toString();
                //if (condicion==""){

                    Intent compartir = new Intent(Intent.ACTION_SEND);
                    startActivity(Intent.createChooser(compartir, "Selecciona Dropbox o Google Drive"));

                    Snackbar.make(view, "Selecciona la carpeta de fotos definida para tu obra actual", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                //}
                //else{Snackbar.make(view, "Favor de generar una imagen", Snackbar.LENGTH_LONG).setAction("Action", null).show();}
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action

            Toast.makeText(this, "Coloca la cámara horizontalmente para obtener una mejor captura", Toast.LENGTH_LONG).show();
            intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intentCamera.resolveActivity(getPackageManager()) != null) {
                File foto = null;
                try {
                    foto = ObtenerNombreFoto();
                } catch (IOException ex) {
                    Toast.makeText(this, "Error compa", Toast.LENGTH_LONG).show();
                }
                if (foto != null) {
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(foto));
                    startActivityForResult(intentCamera, CAMARA);

                }
            }
            /**File carpetafoto= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
             File rutafoto= new File(carpetafoto,"idcamara");
             rutafoto.mkdirs();
             File nombrefoto =new File(rutafoto,"idcam-"+fecha+".jpg");
             tituloValue=nombrefoto.toString();
             Uri guardarfoto =Uri.fromFile(nombrefoto);
             intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, guardarfoto);
             startActivityForResult(intentCamera, CAMERA_PIC_REQUEST);**/
        } else if (id == R.id.nav_gallery) {
             intentGaleria = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intentGaleria.setType("image/*");
            startActivityForResult(Intent.createChooser(intentGaleria, "Selecciona de dónde obtener la imagen"), GALERIA);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case CAMARA:
                if (resultCode==RESULT_OK){
                    procesoFoto();
                }
                break;
            case GALERIA:
                if (resultCode==RESULT_OK){
                    Uri rutafoto = data.getData();
                    tituloValue=rutafoto.toString();
                    titulofoto.setText(tituloValue);
                    visor.setImageURI(rutafoto);
                }
                break;
        }
    }

    public File ObtenerNombreFoto() throws IOException {

        File carpetafoto = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        fechafoto=fecha.format(new Date());
        nombrefoto = "idcamara_" + fechafoto + "_";
        fototemp = File.createTempFile(nombrefoto, ".jpg", carpetafoto);
        tituloValue=fototemp.toString();
        mCurrentPhotoPath = "file:" + fototemp.getAbsolutePath();
        return fototemp;
    }

    public void agregartoGaleria() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File filefoto = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(filefoto);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void procesoFoto() {

        if (mCurrentPhotoPath != null) {
            ponerFoto();
            agregartoGaleria();
            mCurrentPhotoPath = null;
        }

    }

    public void ponerFoto() {
        titulofoto.setText(tituloValue);
        bmp=BitmapFactory.decodeFile(tituloValue);
        visor.setImageBitmap(bmp);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

    }

}