package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import in.aabhasjindal.otptextview.OTPListener;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.ActivityChangePinBinding;

public class ChangePinActivity extends BaseActivity {
ActivityChangePinBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_change_pin);
        inits();
    }

    private void inits() {

        binding.etOldPin.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {

                if (otp.length()>0)
                {
                    binding.lblError.setText("");
                }
            }
        });

        binding.etNewPin.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {

                if (otp.length()>0)
                {
                    binding.lblError.setText("");
                }
            }
        });

        binding.etConfirmPin.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {

                if (otp.length()>0)
                {
                    binding.lblError.setText("");
                }
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid())
                {
                    hide_keyboard();
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_change_pin),Toast.LENGTH_SHORT).show();
                    LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.PIN,binding.etConfirmPin.getOTP());
                    finish();
                }
            }
        });
    }

    private boolean isValid() {

        if (binding.etOldPin.getOTP().isEmpty())
        {
            binding.lblError.setText(getResources().getString(R.string.txt_enter_old_pin));
            return false;
        }
        else if (!binding.etOldPin.getOTP().equalsIgnoreCase(LeafPreference.getInstance(this).getString(LeafPreference.PIN)))
        {
            binding.lblError.setText(getResources().getString(R.string.txt_old_pin_wrong));
            return false;
        }
        else if (binding.etNewPin.getOTP().isEmpty() || binding.etNewPin.getOTP().length()<4)
        {
            binding.lblError.setText(getResources().getString(R.string.txt_enter_new_pin));
            return false;
        }
        else if (binding.etConfirmPin.getOTP().isEmpty() || binding.etConfirmPin.getOTP().length()<4)
        {
            binding.lblError.setText(getResources().getString(R.string.txt_enter_confirm_pin));
            return false;
        }
        else if (!binding.etConfirmPin.getOTP().equalsIgnoreCase(binding.etNewPin.getOTP()))
        {
            binding.lblError.setText(getResources().getString(R.string.txt_confirm_pin_not_match));
            return false;
        }

        return true;
    }
}