package com.example.basicdrawingapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {


    private  var drawingView : DrawingView? = null
    private var mImageButtonCurrentPaint: ImageButton? =null

    var openGalleryLauncher:ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result -> if(result.resultCode == RESULT_OK && result.data!= null){
         val imageBackground: ImageView = findViewById(R.id.iv_background_image)
        imageBackground.setImageURI(result.data?.data)
        }
    }

    val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permissions ->
            permissions.entries.forEach{
                val permissionName = it.key
                val isGranted = it.value

                if(isGranted)
                {
                    Toast.makeText(this,"You can read the storage now",Toast.LENGTH_SHORT).show()
                    val pickIntent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                       openGalleryLauncher.launch(pickIntent)

                }else{
                    if(permissionName == Manifest.permission.READ_EXTERNAL_STORAGE){
                        Toast.makeText(this,"You can NOT read the storage now",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView = findViewById(R.id.drawing_view)
        drawingView?.setSizeForBrush(20.toFloat())

        val linearLayoutPaintColors = findViewById<LinearLayout>(R.id.ll_paint_colours)

        mImageButtonCurrentPaint = linearLayoutPaintColors[4] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this,R.drawable.pallet_selected)
        )

        val ibUndo : ImageButton = findViewById(R.id.undo_btn)
        ibUndo.setOnClickListener {
                 drawingView?.onClickUndo()
        }

        val ibBrush :ImageButton = findViewById(R.id.ib_brush)
        ibBrush.setOnClickListener{
            showBrushSizeChooserDialog()
        }

        val ibGallery : ImageButton = findViewById(R.id.gallery_btn)
        ibGallery.setOnClickListener {
     requestStorageFunction()
        }


    }

    private fun requestStorageFunction(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(
                this,Manifest.permission.READ_EXTERNAL_STORAGE
        )){
           showRationaleDialog("Kids Drawing App","App requires storage permission to use background images")
        }else{
           requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            // Todo write external storage permission
        }

    }





    private fun showBrushSizeChooserDialog(){
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialogue_brush_size)
        brushDialog.setTitle("Brush Size : ")
        val smallBtn: ImageView = brushDialog.findViewById(R.id.ib_small_brush)
        smallBtn.setOnClickListener{
            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }

        val mediumBtn: ImageView = brushDialog.findViewById(R.id.ib_medium_brush)
        mediumBtn.setOnClickListener{
            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }
        val largeBtn: ImageView = brushDialog.findViewById(R.id.ib_large_brush)
        largeBtn.setOnClickListener{
            drawingView?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }

        brushDialog.show()
    }

    fun paintClicked (view : View){
        if(view !== mImageButtonCurrentPaint){
            val imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            drawingView?.setColor(colorTag)

            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_selected)
            )
            mImageButtonCurrentPaint?.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_normal)
            )
            mImageButtonCurrentPaint = view

        }
    }
    private fun showRationaleDialog(title:String, message: String)
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel"){
                dialog,_->dialog.dismiss()
            }
        builder.create().show()
    }


}