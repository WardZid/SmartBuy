package com.example.smartbuy01.control.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.smartbuy01.R;
import com.example.smartbuy01.control.Retrofit.INodeJS;
import com.example.smartbuy01.control.Retrofit.RetrofitClient;
import com.example.smartbuy01.model.User;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SignInActivity extends AppCompatActivity {

    //server api
    private INodeJS myAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    //attr
    private MaterialEditText emailEditText, passwordEditText;
    private Button registerButton,signInButton;

    @Override
    protected void onStop(){
        compositeDisposable.clear();
        super.onStop();
    }
    @Override
    protected void onDestroy(){
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        //to make sure the activity doesnt run in the background and wastes precious memory
        User.signOut();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //init api
        Retrofit retrofit = RetrofitClient.getScalarsInstance();
        myAPI = retrofit.create(INodeJS.class);

        //view
        signInButton=(Button)findViewById(R.id.signInButton);
        registerButton=(Button)findViewById(R.id.registerButton);

        emailEditText=(MaterialEditText)findViewById(R.id.emailEditText);
        passwordEditText=(MaterialEditText)findViewById(R.id.passwordEditText);

        emailEditText.addTextChangedListener(signInWatcher);
        passwordEditText.addTextChangedListener(signInWatcher);

        setUpButtons();

    }//end of onCreate

    private void setUpButtons(){

        //Event
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser(emailEditText.getText().toString(),passwordEditText.getText().toString());

            }
        });//end of signInButton.setOnClickListener

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(emailEditText.getText().toString(),passwordEditText.getText().toString());
            }
        });//end of registerButton.setOnClickListener
    }

    private TextWatcher signInWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //enable/disable buttons if the inputs are empty or not
            String emailInput = emailEditText.getText().toString().trim();
            String passwordInput = passwordEditText.getText().toString().trim();

            signInButton.setEnabled(!emailInput.isEmpty() && !passwordInput.isEmpty());
            registerButton.setEnabled(!emailInput.isEmpty() && !passwordInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void registerUser(final String email, final String password) {
        //with register we need one more param than sign in, which is name
        //so instead of making separate layout for register,
        //i will use dialog box to take name after filling in email+password

        final View enter_name_View= LayoutInflater.from(this).inflate(R.layout.enter_name_layout,null);

        new MaterialStyledDialog.Builder(this)
                .setTitle("Register")
                .setDescription("One more step")
                .setCustomView(enter_name_View)
                .setIcon(R.drawable.ic_user)
                .setNegativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveText("Register")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //get value from edit name
                        MaterialEditText nameEditText = (MaterialEditText)enter_name_View.findViewById(R.id.nameEditText);

                        compositeDisposable.add(myAPI.registerUser(email,nameEditText.getText().toString(),password)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    Toast.makeText(SignInActivity.this,""+s,Toast.LENGTH_SHORT).show();
                                }
                            })//end of subscribeOn
                        );//end of add
                    }//end of onClick
                }).show();//end of builder
    }

    private void signInUser(String email, String password){
        compositeDisposable.add(myAPI.signInUser(email,password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    /*
                    * in our sign in method (index.js), if we enter true password,
                    * we get full user info in json form.
                    * so here we check if string return result has:
                    * 'encrypted_password', 'salt', 'email, ..., etc. in the json string
                    * which would mean succeeded*/
                    System.out.println(s);
                    if(s.contains("encrypted_password")){
                        Toast.makeText(SignInActivity.this,"Sign In Success",Toast.LENGTH_SHORT).show();
                        signIn(s);
                        finish();
                    }
                    else
                        Toast.makeText(SignInActivity.this,""+s,Toast.LENGTH_SHORT).show();//else just show error message

                }//end of accept
            })//end of subscribe
        );//end of compositeDisposable.add
    }//end of signInUser
    private void signIn(String json){
        try {
            //parsing the json object into normal strig values
            JSONObject jsonObj = new JSONObject(json);
            String uuid=jsonObj.getString("user_id");
            String name=jsonObj.getString("name");
            String email=jsonObj.getString("email");
            String createdAt=jsonObj.getString("created_at");
            User.setUser(uuid,name,email,createdAt);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}//end of SignInActivity
