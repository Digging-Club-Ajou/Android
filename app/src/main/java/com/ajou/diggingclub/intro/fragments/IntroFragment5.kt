package com.ajou.diggingclub.intro.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.ajou.diggingclub.MainActivity
import com.ajou.diggingclub.databinding.FragmentIntro4Binding
import com.ajou.diggingclub.databinding.FragmentIntro5Binding

class IntroFragment5 : Fragment() {

    private var _binding : FragmentIntro5Binding?= null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentIntro5Binding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: IntroFragment5Args by navArgs()
        binding.welcome.text = "@${args.nickname} 님의 가입을 환영합니다."

        binding.nextBtn.setOnClickListener {
            val intent = Intent(requireContext(),MainActivity::class.java)
            startActivity(intent)
        }


    }
}