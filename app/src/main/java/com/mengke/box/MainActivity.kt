package com.mengke.box

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.media.RingtoneManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : Activity() {

    private lateinit var prefs: android.content.SharedPreferences
    private var nfcAdapter: NfcAdapter? = null
    private var registerMode = false

    private lateinit var hintText: TextView
    private lateinit var registerBtn: Button
    private lateinit var recycler: RecyclerView
    private lateinit var overlay: View
    private lateinit var overlayName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = getSharedPreferences("mengke", Context.MODE_PRIVATE)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        hintText = findViewById(R.id.hintText)
        registerBtn = findViewById(R.id.registerBtn)
        recycler = findViewById(R.id.recycler)
        overlay = findViewById(R.id.captureOverlay)
        overlayName = findViewById(R.id.overlayName)

        registerBtn.setOnClickListener {
            registerMode = !registerMode
            registerBtn.text = if (registerMode) "取消录入" else "录入新卡片"
            hintText.text = if (registerMode)
                "录入模式：把新卡片贴近手机背面感应区"
            else
                "把卡片贴近手机背面NFC感应区"
        }
        overlay.setOnClickListener { overlay.visibility = View.GONE }

        recycler.layoutManager = LinearLayoutManager(this)
        refreshList()

        when {
            nfcAdapter == null -> hintText.text = "这台手机不支持NFC"
            nfcAdapter?.isEnabled == false -> hintText.text = "请先在设置里打开NFC开关"
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableReaderMode(
            this,
            { tag -> runOnUiThread { onCardScanned(tag) } },
            NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_NFC_F or
                NfcAdapter.FLAG_READER_NFC_V or
                NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null
        )
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    private fun uidOf(tag: Tag): String =
        tag.id.joinToString("") { "%02X".format(it) }

    private fun onCardScanned(tag: Tag) {
        vibrate()
        val uid = uidOf(tag)
        val name = prefs.getString("card_$uid", null)

        if (registerMode) {
            showRegisterDialog(uid, name)
            return
        }
        if (name == null) {
            Toast.makeText(this, "未登记的卡片（卡号 $uid），请先用「录入新卡片」登记", Toast.LENGTH_LONG).show()
            return
        }
        val captured = prefs.getStringSet("captured", emptySet())!!.toMutableSet()
        captured.add(uid)
        prefs.edit().putStringSet("captured", captured).apply()
        showCapture(name)
        refreshList()
    }

    private fun showRegisterDialog(uid: String, oldName: String?) {
        val input = EditText(this).apply { setText(oldName ?: "") }
        AlertDialog.Builder(this)
            .setTitle("录入卡片（卡号 $uid）")
            .setView(input)
            .setPositiveButton("保存") { _, _ ->
                val n = input.text.toString().trim()
                if (n.isNotEmpty()) {
                    prefs.edit().putString("card_$uid", n).apply()
                    Toast.makeText(this, "已保存：$n", Toast.LENGTH_SHORT).show()
                    refreshList()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showCapture(name: String) {
        overlayName.text = "捕捉成功！\n$name"
        overlay.visibility = View.VISIBLE
        val anim = ScaleAnimation(
            0.2f, 1f, 0.2f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply { duration = 400 }
        overlayName.startAnimation(anim)
        playSound()
    }

    private fun playSound() {
        try {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            RingtoneManager.getRingtone(this, uri)?.play()
        } catch (_: Exception) { }
    }

    @Suppress("DEPRECATION")
    private fun vibrate() {
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v.vibrate(200)
        }
    }

    private fun refreshList() {
        val captured = prefs.getStringSet("captured", emptySet())!!.toList().sorted()
        val items = captured.mapNotNull { uid ->
            prefs.getString("card_$uid", null)?.let { name -> name to uid }
        }
        recycler.adapter = MengkeAdapter(items)
    }

    class MengkeAdapter(private val items: List<Pair<String, String>>) :
        RecyclerView.Adapter<MengkeAdapter.VH>() {

        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val name: TextView = v.findViewById(R.id.itemName)
            val uid: TextView = v.findViewById(R.id.itemUid)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            VH(LayoutInflater.from(parent.context).inflate(R.layout.item_mengke, parent, false))

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.name.text = items[position].first
            holder.uid.text = "卡号 ${items[position].second}"
        }
    }
}
