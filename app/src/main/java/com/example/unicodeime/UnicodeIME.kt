package com.example.unicodeime

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class UnicodeIME : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private lateinit var keyboardView: KeyboardView
    private lateinit var keyboard: Keyboard
    private lateinit var previewText: TextView
    private var composingText: StringBuilder = StringBuilder()

    override fun onCreate() {
        super.onCreate()
        window.window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    override fun onCreateInputView(): View {
        val rootView = layoutInflater.inflate(R.layout.keyboard_view, null)
        keyboardView = rootView.findViewById(R.id.keyboard)
        previewText = rootView.findViewById(R.id.preview_text)

        keyboard = Keyboard(this, R.xml.unicode_keyboard)
        keyboardView.keyboard = keyboard
        keyboardView.setOnKeyboardActionListener(this)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.updatePadding(bottom = navInsets.bottom)
            insets
        }

        return rootView
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val ic: InputConnection? = currentInputConnection
        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> {
                if (composingText.isNotEmpty()) {
                    composingText.deleteCharAt(composingText.length - 1)
                    ic?.setComposingText(composingText, 1)
                    updatePreview()
                } else {
                    ic?.deleteSurroundingText(1, 0)
                }
            }
            Keyboard.KEYCODE_DONE, 10 -> {
                val unicodeChar = parseUnicode(composingText.toString())
                if (unicodeChar != null) {
                    ic?.commitText(unicodeChar, 1)
                } else {
                    ic?.commitText(composingText.toString(), 1)
                }
                composingText.clear()
                updatePreview()
            }
            else -> {
                val char = primaryCode.toChar()
                composingText.append(char)
                ic?.setComposingText(composingText, 1)
                updatePreview()
            }
        }
    }

    private fun updatePreview() {
        val unicodeChar = parseUnicode(composingText.toString())
        previewText.text = "Code: $composingText\nChar: ${unicodeChar ?: "-"}"
    }

    private fun parseUnicode(input: String): String? {
        val trimmed = input.trim().replace("""[\s_\-+]""".toRegex(), "")
        val hexPart = trimmed.removePrefix("u").removePrefix("U+")
        return try {
            val codePoint = hexPart.toInt(16)
            if (Character.isValidCodePoint(codePoint)) {
                String(Character.toChars(codePoint))
            } else null
        } catch (e: NumberFormatException) {
            null
        }
    }

    fun vibrateKeyPress(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(17, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
    override fun onPress(primaryCode: Int) {
        vibrateKeyPress(this)
        keyboardView.invalidateAllKeys() // 可添加视觉高亮效果
    }

    override fun onRelease(primaryCode: Int) {
        keyboardView.invalidateAllKeys() // 释放时刷新键盘状态
    }

    override fun onText(text: CharSequence?) {}
    override fun swipeLeft() {}
    override fun swipeRight() {}
    override fun swipeDown() {}
    override fun swipeUp() {}
}