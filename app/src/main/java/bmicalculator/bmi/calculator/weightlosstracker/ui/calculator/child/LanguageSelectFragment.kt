package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentLanguageSelectBinding
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.configDatabase.AppDataBase
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.ViewModelFactory
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel
import com.gyf.immersionbar.ktx.immersionBar


private const val TAG = "Language"

class LanguageSelectFragment : DialogFragment(), View.OnClickListener {

    private lateinit var binding: FragmentLanguageSelectBinding

    private lateinit var currentCard: ImageView

    private lateinit var viewModel: CalculatorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLanguageSelectBinding.inflate(layoutInflater, container, false)
        val dao = AppDataBase.getDatabase(requireContext().applicationContext).bmiInfoDao()
        val repository = Repository(dao)
        val factory = ViewModelFactory(repository, requireActivity())
        viewModel =
            ViewModelProvider(requireActivity(), factory)[CalculatorViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        val lan = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString(
                "language", "en"
            )
        currentCard = getCurrentLanguageSelectedImage(lan.toString())

        binding.lcard1.setOnClickListener(this)
        binding.lcard2.setOnClickListener(this)
        binding.lcard3.setOnClickListener(this)
        binding.lcard4.setOnClickListener(this)
        binding.lcard5.setOnClickListener(this)
        binding.lcard6.setOnClickListener(this)
        binding.lcard7.setOnClickListener(this)
        binding.lcard8.setOnClickListener(this)
        binding.lcard9.setOnClickListener(this)
        binding.lcard10.setOnClickListener(this)
        binding.lcard11.setOnClickListener(this)

        binding.languageBack.setOnClickListener {
            onDestroyView()
        }

    }


    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.MATCH_PARENT
        params?.gravity = Gravity.BOTTOM
        params?.dimAmount = 0.0f
        dialog?.window?.attributes = params as WindowManager.LayoutParams
        immersionBar {
            statusBarColor(R.color.bg1)
            statusBarDarkFont(true)
            titleBar(view)
        }
    }

    private fun getCurrentLanguageSelectedImage(language: String): ImageView {
        when (language) {
            "en" -> {
                binding.lcard1Img.visibility = View.VISIBLE
                return binding.lcard1Img
            }

            "zh-rCN" -> {
                binding.lcard7Img.visibility = View.VISIBLE
                return binding.lcard7Img
            }

            "pt" -> {
                binding.lcard2Img.visibility = View.VISIBLE
                return binding.lcard2Img
            }

            "ru" -> {
                binding.lcard3Img.visibility = View.VISIBLE
                return binding.lcard3Img
            }

            "de" -> {
                binding.lcard5Img.visibility = View.VISIBLE
                return binding.lcard5Img
            }

            "zh-rTW" -> {
                binding.lcard6Img.visibility = View.VISIBLE
                return binding.lcard6Img
            }

            "fr" -> {
                binding.lcard8Img.visibility = View.VISIBLE
                return binding.lcard8Img
            }

            "es" -> {
                binding.lcard9Img.visibility = View.VISIBLE
                return binding.lcard9Img
            }

            "it" -> {
                binding.lcard10Img.visibility = View.VISIBLE
                return binding.lcard10Img
            }

            "ko" -> {
                binding.lcard11Img.visibility = View.VISIBLE
                return binding.lcard11Img
            }

            else -> {
                throw Exception("language is null")
            }
        }
    }

    override fun onClick(p0: View?) {
        val edit = activity?.getSharedPreferences("settings", 0)?.edit()
        when (p0!!.id) {
            R.id.lcard_1 -> {
                binding.lcard1Img.visibility = View.VISIBLE
                if (currentCard != binding.lcard1Img) {
                    currentCard.visibility = View.INVISIBLE
                }
                currentCard = binding.lcard1Img
                edit?.putString("language", "en")
                edit?.apply()
                activity?.recreate()
            }

            R.id.lcard_2 -> {
                binding.lcard2Img.visibility = View.VISIBLE
                if (currentCard != binding.lcard2Img) {
                    currentCard.visibility = View.INVISIBLE
                }
                currentCard = binding.lcard2Img
                edit?.putString("language", "pt")
                edit?.apply()
                activity?.recreate()
            }

            R.id.lcard_3 -> {
                binding.lcard3Img.visibility = View.VISIBLE
                if (currentCard != binding.lcard3Img) {
                    currentCard.visibility = View.INVISIBLE
                }
                currentCard = binding.lcard3Img
                edit?.putString("language", "ru")
                edit?.apply()
                activity?.recreate()
            }

            R.id.lcard_4 -> {
                binding.lcard4Img.visibility = View.VISIBLE
                if (currentCard != binding.lcard4Img) {
                    currentCard.visibility = View.INVISIBLE
                }
                currentCard = binding.lcard4Img
            }

            R.id.lcard_5 -> {
                binding.lcard5Img.visibility = View.VISIBLE
                if (currentCard != binding.lcard5Img) {
                    currentCard.visibility = View.INVISIBLE
                }
                currentCard = binding.lcard5Img
                edit?.putString("language", "de")
                edit?.apply()
                activity?.recreate()
            }

            R.id.lcard_6 -> {
                binding.lcard6Img.visibility = View.VISIBLE
                if (currentCard != binding.lcard6Img) {
                    currentCard.visibility = View.INVISIBLE
                }
                currentCard = binding.lcard6Img
                edit?.putString("language", "zh-rTW")
                edit?.apply()
                activity?.recreate()
            }

            R.id.lcard_7 -> {
                binding.lcard7Img.visibility = View.VISIBLE
                if (currentCard != binding.lcard7Img) {
                    currentCard.visibility = View.INVISIBLE
                }
                currentCard = binding.lcard7Img
                edit?.putString("language", "zh-rCN")
                edit?.apply()
                activity?.recreate()
            }

            R.id.lcard_8 -> {
                binding.lcard8Img.visibility = View.VISIBLE
                if (currentCard != binding.lcard8Img) {
                    currentCard.visibility = View.INVISIBLE
                }
                currentCard = binding.lcard8Img
                edit?.putString("language", "fr")
                edit?.apply()
                activity?.recreate()
            }

            R.id.lcard_9 -> {
                binding.lcard9Img.visibility = View.VISIBLE
                if (currentCard != binding.lcard9Img) {
                    currentCard.visibility = View.INVISIBLE
                }
                currentCard = binding.lcard9Img
                edit?.putString("language", "es")
                edit?.apply()
                activity?.recreate()
            }

            R.id.lcard_10 -> {
                binding.lcard10Img.visibility = View.VISIBLE
                if (currentCard != binding.lcard10Img) {
                    currentCard.visibility = View.INVISIBLE
                }
                currentCard = binding.lcard10Img
                edit?.putString("language", "it")
                edit?.apply()
                activity?.recreate()
            }

            R.id.lcard_11 -> {
                binding.lcard11Img.visibility = View.VISIBLE
                if (currentCard != binding.lcard11Img) {
                    currentCard.visibility = View.INVISIBLE
                }
                currentCard = binding.lcard11Img
                edit?.putString("language", "ko")
                edit?.apply()
                activity?.recreate()
            }
        }
    }
}