package eina.unizar.ajedrez;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class UserSignUp extends AppCompatActivity {

    private EditText mUsername;
    private EditText mUserFullName;
    private EditText mUserPassword;
    private EditText mUserPasswordRepeat;
    private EditText mUserMail;
    private Long mRowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mDbHelper = new NotesDbAdapter(this);
        //mDbHelper.open();
        // setContentView(R.layout.note_edit);
        // setTitle(R.string.edit_note);

        mUsername = (EditText) findViewById(R.id.username);
        mUserFullName = (EditText) findViewById(R.id.userFullname);
        mUserPassword = (EditText) findViewById(R.id.password);
        mUserPasswordRepeat = (EditText) findViewById(R.id.passwordRepeat);
        mUserMail = (EditText) findViewById(R.id.mail);

        Button confirmButton = (Button) findViewById(R.id.submit);

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });
        /*mRowId = (savedInstanceState == null) ? null:
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null) ?
                    extras.getLong(NotesDbAdapter.KEY_ROWID) : null;
        }*/


    }
}
