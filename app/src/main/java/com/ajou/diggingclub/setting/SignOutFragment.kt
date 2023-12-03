package com.ajou.diggingclub.setting

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.FragmentSignOutBinding

class SignOutFragment : Fragment() {
    private var mContext : Context? = null
    private var _binding : FragmentSignOutBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog : SignOutDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignOutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logout.setOnClickListener {
            dialog = SignOutDialog(mContext!!)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

        binding.withdrawal.setOnClickListener {
            findNavController().navigate(R.id.action_signOutFragment_to_withdrawalFragment)
        }

        binding.backBtn.setOnClickListener {
            val intent = Intent(mContext!!, SettingActivity::class.java)
            startActivity(intent)
        }
    }

}