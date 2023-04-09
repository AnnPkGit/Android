package step.learning.course;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class CalcActivity extends AppCompatActivity {

    private TextView tvHistory ;
    private TextView tvResult ;
    private String minusSign ;
    private String zeroSymbol ;
    private String commaSymbol ;

    private String multiplySymbol ;

    private String divideSymbol ;
    private boolean needClear ;  // необходимо очистить экран при вводе новой цифры
    private boolean commaAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        minusSign = getString( R.string.calc_minus_sign ) ;
        zeroSymbol = getString( R.string.calc_btn_0_text ) ;
        commaSymbol = getString( R.string.calc_btn_comma_text ) ;
        multiplySymbol = getString( R.string.calc_btn_multiplication_text ) ;
        divideSymbol = getString( R.string.calc_btn_divide_text) ;

        tvHistory = findViewById( R.id.tv_history ) ;
        tvResult = findViewById( R.id.tv_result ) ;

        clearClick( null ) ;

        // String[] suffixes = {"one", "two", } ;
        for( int i = 0; i < 10; i++ ) {
            @SuppressLint("DiscouragedApi")   // более эффективно - R.id.calc_btn_5
            int buttonId = getResources().getIdentifier(
                    "calc_btn_" + i,    // suffixes[i],
                    "id",
                    getPackageName()
            ) ;
            findViewById( buttonId ).setOnClickListener( this::digitClick ) ;
        }
        findViewById( R.id.calc_btn_backspace ).setOnClickListener( this::backspaceClick ) ;
        findViewById( R.id.calc_btn_plus_minus ).setOnClickListener( this::plusMinusClick ) ;
        findViewById( R.id.calc_btn_clear ).setOnClickListener( this::clearClick ) ;
        findViewById( R.id.calc_btn_ce ).setOnClickListener( this::clearEditClick ) ;
        findViewById( R.id.calc_btn_square ).setOnClickListener( this::squareClick ) ;
        findViewById( R.id.calc_btn_comma ).setOnClickListener( this::commaClick ) ;
        findViewById( R.id.calc_btn_inverse ).setOnClickListener( this::inverseClick ) ;

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savingState) {
        super.onSaveInstanceState(savingState);
        Log.d("CalcActivity", "onSaveInstanceState");
        savingState.putCharSequence("history", tvHistory.getText());
        savingState.putCharSequence("result", tvResult.getText());
        savingState.putCharSequence("needClear", String.valueOf(needClear));
        savingState.putCharSequence("commaAdded", String.valueOf(commaAdded));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        Log.d("CalcActivity", "onRestoreInstanceState");
        tvHistory.setText(savedState.getCharSequence("history"));
        tvResult.setText(savedState.getCharSequence("result"));
        needClear = Boolean.parseBoolean(savedState.getCharSequence("needClear").toString());
        commaAdded = Boolean.parseBoolean(savedState.getCharSequence("commaAdded").toString());
    }

    private void squareClick(View view ) {
        String result = tvResult.getText().toString() ;
        double arg ;
        try {
             arg = Double.parseDouble(
                    result
                            .replace( minusSign, "-" )
                            .replaceAll( zeroSymbol, "0" )
             ) ;
        }
        catch( NumberFormatException | NullPointerException ignored ) {
            Toast.makeText(                     // Всплывающее сообщение
                    this,                       // контекст - родительская активность
                    R.string.calc_error_parse,  // текст либо ресурс
                    Toast.LENGTH_SHORT )        // длительность (во времени)
                .show();                        // !! не забывать - запуск тоста
            return ;
        }
        tvHistory.setText(getString(R.string.calc_square_history, result )) ;
        arg *= arg ;
        displayResult( arg ) ;
        needClear = true ;
    }
    private void clearClick( View view ) {  // C
        Clear();
    }
    private void clearEditClick( View view ) {  // CE
        displayResult( "" ) ;
    }
    private void plusMinusClick( View view ) {
        // изменение знака: если есть "-" перед числом, то убираем его, если нет - добавляем
        String result = tvResult.getText().toString() ;
        if( result.equals( zeroSymbol ) ) {
            return ;   // перед "0" знак не ставить
        }
        if( result.startsWith( minusSign ) ) {
            result = result.substring( 1 ) ;
        }
        else {
            result = minusSign + result ;
        }
        displayResult( result ) ;
    }
    private void backspaceClick( View view ) {
        if(needClear) {
            Clear();
            return;
        }

        String result = tvResult.getText().toString() ;
        if (result.endsWith(commaSymbol)) {
            commaAdded = false;
        }
        result = result.substring(0, result.length() - 1);
        displayResult( result ) ;
    }
    private void digitClick( View view ) {
        String result = tvResult.getText().toString() ;
        if( getResultLengthOnlyDigits() >= 10 ) {
            return ;
        }
        String digit = ( (Button) view ).getText().toString() ;
        if( result.equals( zeroSymbol ) || needClear ) {
            result = "" ;
            needClear = false ;
        }
        result += digit ;
        displayResult( result ) ;
// Ø
    }

    private void commaClick( View view ) {
        if(commaAdded) return;

        String result = tvResult.getText().toString() ;
        if( getResultLengthOnlyDigits() >= 10 ) {
            return ;
        }
        result += ( (Button) view ).getText().toString() ;
        displayResult( result ) ;
        commaAdded = true;
    }

    private void inverseClick( View view ) {
        String result = tvResult.getText().toString() ;
        if(result.equals(zeroSymbol)) {
            result = 1 + divideSymbol;
        }
        else {
            result += multiplySymbol + 1 + divideSymbol;
        }
        displayResult(result);
    }

    private void displayResult( String result ) {
        if( "".equals( result ) || minusSign.equals( result ) ) {
            result = zeroSymbol ;
        }
        tvResult.setText( result ) ;
    }
    private void displayResult( double arg ) {
        long argInt = (long) arg ;
        String result = argInt == arg ? "" + argInt : "" + arg ;

        result = result
                .replace( "-", minusSign )
                .replaceAll( "0", zeroSymbol ) ;

        displayResult( result ) ;
    }

    private int getResultLengthOnlyDigits() {
        String resultString = tvResult.getText().toString();

        resultString = resultString.replaceAll(commaSymbol, "")
                .replaceAll(minusSign, "");
        return resultString.length();
    }

    private void Clear() {
        tvHistory.setText( "" ) ;
        displayResult( "" ) ;
    }
}
/*
Д.З. Реализовать работу кнопки "," (десятичная запятая)
- символ запятой задается ресурсами (подобрать симпатичный символ)
- запятая в числе только одна
- в общий лимит цифр числа (10 цифр) символы "-" и "," не включается
 */