package id.co.binar.secondhand.util

import com.google.android.material.textfield.TextInputLayout
import id.co.binar.secondhand.R
import io.github.anderscheow.validator.Validation
import io.github.anderscheow.validator.rules.common.maximumLength
import io.github.anderscheow.validator.rules.common.minimumLength
import io.github.anderscheow.validator.rules.common.notEmpty
import io.github.anderscheow.validator.rules.common.notNull
import io.github.anderscheow.validator.rules.regex.PasswordRule
import io.github.anderscheow.validator.rules.regex.email
import io.github.anderscheow.validator.rules.regex.withPassword
import io.github.anderscheow.validator.validation

fun emailValid(textInputLayout: TextInputLayout): Validation {
    return validation(textInputLayout) {
        rules {
            +notNull(R.string.txt_not_null)
            +notEmpty(R.string.txt_not_empty)
            +email(R.string.txt_not_email)
        }
    }
}

fun passwordValid(textInputLayout: TextInputLayout): Validation {
    return validation(textInputLayout) {
        rules {
            +notNull(R.string.txt_not_null)
            +notEmpty(R.string.txt_not_empty)
            +minimumLength(8, R.string.txt_not_min_length_8)
            +withPassword(PasswordRule.PasswordRegex.ALPHA_MIXED_CASE, R.string.txt_not_lowercase_and_uppercase)
        }
    }
}

fun phoneValid(textInputLayout: TextInputLayout): Validation {
    return validation(textInputLayout) {
        rules {
            +notNull(R.string.txt_not_null)
            +notEmpty(R.string.txt_not_empty)
            +maximumLength(16, R.string.length_of_phone)
        }
    }
}

fun priceValid(textInputLayout: TextInputLayout): Validation {
    return validation(textInputLayout) {
        rules {
            +notNull(R.string.txt_not_null)
            +notEmpty(R.string.txt_not_empty)
            +maximumLength(13, R.string.length_of_price)
        }
    }
}

fun generalValid(textInputLayout: TextInputLayout): Validation {
    return validation(textInputLayout) {
        rules {
            +notNull(R.string.txt_not_null)
            +notEmpty(R.string.txt_not_empty)
        }
    }
}