package com.getyoteam.budamind.activity

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.centerprime.binance_smart_chain_sdk.BinanceManager
import com.getyoteam.budamind.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_receive.ivHeaderLeft
import kotlinx.android.synthetic.main.activity_send_token.*
import java.math.BigDecimal
import java.math.BigInteger

class SendTokenActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_token)


        ivHeaderLeft.setOnClickListener { finish() }

        val binanceManager = BinanceManager.getInstance()

//        binanceManager.importFromKeystore("{\"address\":\"77799e843c0cfd5ba2f5e52c6012b6b5720d5c06\",\"id\":\"42db1db1-aa15-4e50-aca9-1e711e30a0ff\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"cipherparams\":{\"iv\":\"014e1aebe5f0bc94876082bdd8de64aa\"},\"ciphertext\":\"8928c0f1f385b65857d4cc0871bfaa5d18a0ab48675658530ef5d114d15e8d39\",\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":4096,\"p\":6,\"r\":8,\"salt\":\"82e3e27eef8bb3f4011f4a4d687154566822de0f423b29473c8fbe00c3bc83ac\"},\"mac\":\"baff32ab202701ed9c70cac4c9b6687dfea1e361bd69d12bb4d3048025326318\"}}", "raj@123123123", this)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ walletAddress: String ->
//                /**
//                 * if function successfully completes result can be caught in this block
//                 */
//                binding.walletAddress.setText("0x$walletAddress")
//                binding.copy.setVisibility(View.VISIBLE)
//                Toast.makeText(this, "0x$walletAddress", Toast.LENGTH_SHORT).show()
//            }) { error: Throwable ->
//                /**
//                 * if function fails error can be caught in this block
//                 */
//                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
//            }

        /**
         * @param infura - Initialize infura
         */
        binanceManager.init("https://bsc-dataseed1.binance.org:443")
//        binanceManager.init("https://data-seed-prebsc-1-s1.binance.org:8545"); // for test net
        etWalletAddress.setText("0x77799e843c0cfd5ba2f5e52c6012b6b5720d5c06")
        etReciverWalletAddress.setText("0x7cE8E4D987EC5229543b604802d9A1Dcb07d4c80")
//        contractAddress.setText("0x0567F2323251f0Aab15c8dFb1967E4e8A7D42aeE")
        etPassword.setText("raj@123123123")
        tvSend.setOnClickListener {

            if (!TextUtils.isEmpty(etWalletAddress.getText().toString().trim())
                && !TextUtils.isEmpty(etbnbAmount.getText().toString().trim())
                && !TextUtils.isEmpty(gasLimit.getText().toString().trim())
                && !TextUtils.isEmpty(etReciverWalletAddress.getText().toString().trim())
                && !TextUtils.isEmpty(etPassword.getText().toString().trim())
            ) {
                /**
                 * Using this sendToken function you can send BNB from walletAddress to another walletAddress.
                 *
                 * @param walletAddress - must be provided sender's wallet address
                 * @param password - User must enter password of wallet address
                 * @param gasPrice - gas price: 30000000000
                 * @param gasLimit - gas limit atleast 21000 or more
                 * @param tokenAmount - amount of token
                 * @param tokenAmount - amount of BNB which user want to send
                 * @param receiverWalletAddress - wallet address which is user want to send token
                 * @param Context - activity context
                 *
                 * @return if sending token completes successfully the function returns transactionHash or returns error name
                 */
                val walletAddress: String = etWalletAddress.getText().toString().trim()
                val password: String = etPassword.getText().toString().trim()
                val gasPrice = BigInteger(binanceManager.gasPrice.toString())
                val gasLimit: BigInteger = BigInteger(gasLimit.getText().toString().trim())
                val tokenAmount: BigDecimal = BigDecimal(etbnbAmount.getText().toString().trim())
                val receiverAddress: String = etReciverWalletAddress.getText().toString().trim()
                val bep20TokenContractAddress: String = "0x0567F2323251f0Aab15c8dFb1967E4e8A7D42aeE"
                binanceManager.sendToken(
                    walletAddress,
                    password,
                    gasPrice,
                    gasLimit,
                    tokenAmount,
                    receiverAddress,
                    bep20TokenContractAddress,
                    this
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Consumer { tx: String ->
                        /**
                         * if function successfully completes result can be caught in this block
                         */
//                        binding.result.setText(tx)
                        Toast.makeText(this, "TX : $tx", Toast.LENGTH_SHORT).show()
                    }, Consumer { error: Throwable ->
                        /**
                         * if function fails error can be caught in this block
                         */
                        Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                        println("Error: " + error.message)
                    })
            } else {
                Toast.makeText(this, "require all filds", Toast.LENGTH_SHORT).show()
            }
        }


    }

}
