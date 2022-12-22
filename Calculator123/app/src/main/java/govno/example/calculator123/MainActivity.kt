package govno.example.calculator123

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import java.math.RoundingMode.HALF_UP

class MainActivity : AppCompatActivity() {
    var zeroCheck = false
    var commaCheck = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val butt0 = findViewById<Button>(R.id.butt_0)
        val butt_1 = findViewById<Button>(R.id.butt_1)
        val butt_2 = findViewById<Button>(R.id.butt_2)
        val butt_3: Button = findViewById<Button>(R.id.butt_3)
        val butt_4: Button = findViewById<Button>(R.id.butt_4)
        val butt_5 = findViewById<Button>(R.id.butt_5)
        val butt_6 = findViewById<Button>(R.id.butt_6)
        val butt_7 = findViewById<Button>(R.id.butt_7)
        val butt_8 = findViewById<Button>(R.id.butt_8)
        val butt_9 = findViewById<Button>(R.id.butt_9)
        val butt_min = findViewById<Button>(R.id.butt_minus)
        val butt_mult = findViewById<Button>(R.id.butt_multi)
        val butt_div = findViewById<Button>(R.id.butt_divide)
        val butt_plu = findViewById<Button>(R.id.butt_plus)
        val butt_back = findViewById<Button>(R.id.butt_back)
        val butt_ac = findViewById<Button>(R.id.butt_ac)
        val butt_negate = findViewById<Button>(R.id.butt_negate)
        val butt_result = findViewById<Button>(R.id.butt_res)
        val butt_comma = findViewById<Button>(R.id.butt_comma)
        butt0.setOnClickListener {
            setTextFields("0")
        }
        butt_comma.setOnClickListener {
            zeroCheck = false
            setTextFields(".")
            commaCheck = true

        }
        butt_1.setOnClickListener {
            setTextFields("1")
        }
        butt_2.setOnClickListener {
            setTextFields("2")
        }
        butt_3.setOnClickListener {
            setTextFields("3")
        }
        butt_4.setOnClickListener {
            setTextFields("4")
        }
        butt_5.setOnClickListener {
            setTextFields("5")

        }
        butt_6.setOnClickListener {
            setTextFields("6")
        }
        butt_7.setOnClickListener {
            setTextFields("7")

        }
        butt_8.setOnClickListener {
            setTextFields("8")
        }
        butt_9.setOnClickListener {
            setTextFields("9")
        }
        butt_min.setOnClickListener {
            setTextFields("-")
            zeroCheck = false

        }
        butt_plu.setOnClickListener {
            setTextFields("+")
            zeroCheck = false
        }
        butt_mult.setOnClickListener {
            setTextFields("*")
            zeroCheck = false

        }
        butt_div.setOnClickListener {
            setTextFields("/")
            zeroCheck = false

        }
        butt_ac.setOnClickListener {
            val operation = findViewById<TextView>(R.id.operation)
            val res = findViewById<TextView>(R.id.result_txt)
            operation.text = ""
            res.text = ""
            zeroCheck = false
        }
        butt_back.setOnClickListener {
            val operation = findViewById<TextView>(R.id.operation)
            val res = findViewById<TextView>(R.id.result_txt)
            val string = operation.text.toString()
            if (string.isNotEmpty()) {
                if (string[string.length - 1] == '.') {
                    commaCheck = false
                }
                operation.text = string.substring(0, string.length - 1)
            }
            res.text = ""
        }
        butt_result.setOnClickListener {
            val operation = findViewById<TextView>(R.id.operation)
            val res = findViewById<TextView>(R.id.result_txt)
            try {
                val expres = com.nagpal.shivam.expressionparser.Expression(operation.text.toString())
                val ev = expres.evaluate()
                if (ev == ev.toLong().toDouble()) {
                    res.text =
                        ev.toBigDecimal().setScale(5, HALF_UP).stripTrailingZeros().toPlainString()
                } else {
                    res.text =
                        ev.toBigDecimal().setScale(5, HALF_UP).stripTrailingZeros().toPlainString()
                }
            } catch (e: Exception) {
                Log.d("Invalid input", "${e.message}")
            }
            zeroCheck = false

        }
        butt_negate.setOnClickListener {
            val values = arrayOf("+", "-", "*", "/", "0", ".")
            val operation = findViewById<TextView>(R.id.operation)
            if (operation.text.toString() !in values) {
                operation.text = "-" + operation.text
            } else {
                Toast.makeText(this, "Can't be - there)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val operation = findViewById<TextView>(R.id.operation)
        val res = findViewById<TextView>(R.id.result_txt)
        res.text = savedInstanceState.getString("result_txt")
        operation.text = savedInstanceState.getString("operation")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val operation = findViewById<TextView>(R.id.operation)
        val res = findViewById<TextView>(R.id.result_txt)
        outState.putString("result_txt", res.text.toString())
        outState.putString("operation", operation.text.toString())
    }

    fun setTextFields(str: String) {
        val values = arrayOf("+", "-", "*", "/")
        val operation = findViewById<TextView>(R.id.operation)
        when (str) {
            "0" -> {
                if (operation.text.toString().isEmpty()) {
                    zeroCheck = false
                    commaCheck = false
                }
                if (!zeroCheck) {
                    operation.append(str)
                    zeroCheck = !commaCheck
                } else {
                    Toast.makeText(this, "you already put 0", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                when {
                    str != "0" && str != "." -> {
                        if (operation.text.toString().isEmpty()) {
                            zeroCheck = false
                            commaCheck = false
                        }
                        if (str in values) {
                            operation.append(str)
                            commaCheck = false
                        } else {
                            operation.append(str)
                        }
                    }
                    str == "." -> {
                        when {
                            operation.text.toString().isEmpty() -> {
                                commaCheck = true
                            }
                        }
                        if (!commaCheck) {
                            operation.append(str)
                            commaCheck = true
                        }
                    }
                }
            }
        }
    }
}