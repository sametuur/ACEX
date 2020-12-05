package com.yu.stacker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

public class MainActivity extends AppCompatActivity {

    Button button ;
    TextView giris , email;
    AuthHuaweiId huaweiAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        giris = findViewById(R.id.giris);
        email = findViewById(R.id.email);
        huaweiAccount = null;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setEmail().setIdToken().createParams();
                HuaweiIdAuthService service = HuaweiIdAuthManager.getService(MainActivity.this, authParams);
                if(huaweiAccount == null)
                {
                    startActivityForResult(service.getSignInIntent(), 8888);
                }
                else
                {
                    Task<Void> signOutTask;
                    signOutTask = service.signOut();
                    signOutTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            Log.i("TAG", "signOut complete");
                            button.setText("Giriş Yap");
                            giris.setText("Hello Huawei");
                            email.setVisibility(View.INVISIBLE);
                            huaweiAccount = null;
                        }
                    });
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8888) {
            Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            if (authHuaweiIdTask.isSuccessful()) {
                AuthHuaweiId huaweiAccount = authHuaweiIdTask.getResult();
                giris.setText(huaweiAccount.getDisplayName());
                button.setText("Çıkış Yap");
                email.setVisibility(View.VISIBLE);
                email.setText(huaweiAccount.getEmail());
                Log.i("TAG", "idToken:" + huaweiAccount.getIdToken());
            } else {
                Log.e("TAG", "sign in failed : " + ((ApiException) authHuaweiIdTask.getException()).getStatusCode());
            }
        }
    }
}