package ca.uwindsor.calaj.tipster

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
private const val INITIAL_SPLIT = 1
class MainActivity : AppCompatActivity() {
    private lateinit var  etBaseAmount: EditText
    private lateinit var  seekBarTip: SeekBar
    private lateinit var  tvTipPercentLabel: TextView
    private lateinit var  tvTipAmount: TextView
    private lateinit var  tvTotalAmount: TextView
    private lateinit var  tvTipDescription: TextView
    private lateinit var  seekBarSplit: SeekBar
    private lateinit var  tvSplitNum: TextView
    private lateinit var  tvPerPerson: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercentLabel = findViewById(R.id.tvTipPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        seekBarSplit = findViewById(R.id.seekBarSplit)
        tvSplitNum = findViewById(R.id.tvSplitNum)
        tvPerPerson = findViewById(R.id.tvPerPerson)

        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)
        seekBarSplit.progress = INITIAL_SPLIT
        tvSplitNum.text = "$INITIAL_SPLIT \uD83E\uDDCD"
        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                tvTipPercentLabel.text = "$progress%"
                computeTipAndTotal()
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        etBaseAmount.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged $s")
                computeTipAndTotal()
            }
        })
        seekBarSplit.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                val numPpl = progress+1
                tvSplitNum.text = "$numPpl \uD83E\uDDCD"
                computeTipAndTotal()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent){
            in 0..9 -> "\uD83D\uDE21 Poor \uD83D\uDE21"
            in 10..14 -> "\uD83D\uDE44 Acceptable \uD83D\uDE44"
            in 15..19 -> "\uD83D\uDE03 Good \uD83D\uDE03"
            in 20..24 -> "\uD83D\uDE0A Great \uD83D\uDE0A"
            else -> "\uD83D\uDE01 Amazing \uD83D\uDE01"
        }
        tvTipDescription.text = tipDescription
        //Update colour based on tipPercent
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.color_worst_tip),
            ContextCompat.getColor(this, R.color.color_best_tip)
        ) as Int
        tvTipDescription.setTextColor(color)

    }

    private fun computeTipAndTotal() {
        if (etBaseAmount.text.isEmpty()){
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }
        //1. Get value of the base and tip percent as well as # of ppl
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        val numPpls = seekBarSplit.progress + 1
        //2. compute the tip and total
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount
        val totalSplit = totalAmount / numPpls
        //3. update UI
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
        tvPerPerson.text = "%.2f".format(totalSplit)
    }
}