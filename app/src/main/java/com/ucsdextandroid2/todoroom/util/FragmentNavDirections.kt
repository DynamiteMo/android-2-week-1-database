package com.ucsdextandroid2.todoroom.util

import android.os.Bundle
import androidx.navigation.NavDirections

/**
 * Created by rjaylward on 4/5/20
 */

class FragmentNavDirections(
        private val id: Int,
        private val args: Bundle
) : NavDirections {

    override fun getArguments(): Bundle = args

    override fun getActionId(): Int = id
}