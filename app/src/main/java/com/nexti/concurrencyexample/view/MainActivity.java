package com.nexti.concurrencyexample.view;

import static com.nexti.concurrencyexample.viewmodel.ModeEnum.*;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.nexti.concurrencyexample.viewmodel.MainViewModel;
import com.nexti.concurrencyexample.R;
import com.nexti.concurrencyexample.model.ResponseDto;

public class MainActivity extends AppCompatActivity implements ResponseListener {

    private Button btExecute;
    private TextView tvResult;
    private TextView tvResultTime;
    private ProgressBar pbWaiting;
    private Spinner spModeSelector;
    private MainViewModel mainViewModel;
    private TextInputEditText etDelay;
    private TextInputEditText etTimeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViews();
        mainViewModel = new MainViewModel(this);
    }

    private void setViews() {
        setContentView(R.layout.activity_main);
        pbWaiting = findViewById(R.id.pb_waiting);
        btExecute = findViewById(R.id.bt_execute);
        tvResult = findViewById(R.id.tv_result);
        tvResultTime = findViewById(R.id.tv_result_time);
        spModeSelector = findViewById(R.id.sp_selector);
        etDelay = findViewById(R.id.et_delay);
        etTimeout = findViewById(R.id.et_timeout);

        etDelay.setOnEditorActionListener((v, actionId, event) -> {
            try {
                mainViewModel.setDelay(Long.parseLong(v.getText().toString()));
            } catch (Exception e) {
                mainViewModel.setDelay(5L);
            }
            return false;
        });

        etTimeout.setOnEditorActionListener((v, actionId, event) -> {
            try {
                mainViewModel.setTimeout(Long.parseLong(v.getText().toString()));
            } catch (Exception e) {
                mainViewModel.setTimeout(30L);
            }
            return false;
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.modes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spModeSelector.setAdapter(adapter);
        spModeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mainViewModel.setMode(JAVA_THREADS);
                        break;
                    case 1:
                        mainViewModel.setMode(KOTLIN_COROUTINE);
                        break;
                    case 2:
                        mainViewModel.setMode(RX_JAVA);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btExecute.setOnClickListener(v -> {
            pbWaiting.setVisibility(View.VISIBLE);
            tvResult.setVisibility(View.GONE);
            tvResultTime.setVisibility(View.GONE);
            mainViewModel.makeAsyncRequest();
        });
    }

    @Override
    public void processResult(ResponseDto response) {
        runOnUiThread(() -> {
            pbWaiting.setVisibility(View.GONE);
            tvResult.setVisibility(View.VISIBLE);
            tvResult.setText(response.getResponseBody());

            if (response.getRequestTime() != -1L) {
                tvResultTime.setVisibility(View.VISIBLE);
                tvResultTime.setText("Time: " + response.getRequestTime() + " ms");
            }
        });
    }
}