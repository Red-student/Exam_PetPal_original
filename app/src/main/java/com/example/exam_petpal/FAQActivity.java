package com.example.exam_petpal;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;

public class FAQActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        // Настройка Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Добавление FAQ
        addFaq("Как добавить питомца?", "На главном экране нажмите на кнопку '+' или 'Добавить питомца'. Заполните данные и сохраните.");
        addFaq("Как добавить событие или вакцинацию?", "Откройте профиль питомца и выберите вкладку 'События' или 'Вакцины'. Там можно добавить новое событие или вакцинацию.");
        addFaq("Как включить напоминания?", "При добавлении события выберите время напоминания. Приложение само напомнит вам в нужный момент.");
        addFaq("Как скрыть питомца?", "В профиле питомца нажмите 'Скрыть (архивировать)'. Питомец исчезнет из основного списка, но его можно восстановить через меню 'Показать архивных'.");
        addFaq("Как восстановить данные?", "В профиле пользователя есть кнопки для резервного копирования и восстановления из облака.");
        addFaq("Как связаться с поддержкой?", "В профиле пользователя есть кнопка 'Обратная связь'. Вы можете отправить сообщение прямо из приложения.");
    }

    private void addFaq(String question, String answer) {
        // Создаем карточку для FAQ
        MaterialCardView card = new MaterialCardView(this);
        card.setRadius(8f);
        card.setCardElevation(4f);
        card.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Создаем контейнер для содержимого карточки
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(32, 16, 32, 16);
        card.addView(container);

        // Добавляем вопрос
        TextView questionView = new TextView(this);
        questionView.setText(question);
        questionView.setTextSize(18f);
        questionView.setTypeface(null, android.graphics.Typeface.BOLD);
        container.addView(questionView);

        // Добавляем ответ
        TextView answerView = new TextView(this);
        answerView.setText(answer);
        answerView.setTextSize(16f);
        answerView.setPadding(0, 8, 0, 0);
        container.addView(answerView);

        // Добавляем карточку в основной контейнер
        LinearLayout mainContainer = findViewById(R.id.faqContainer);
        mainContainer.addView(card);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 