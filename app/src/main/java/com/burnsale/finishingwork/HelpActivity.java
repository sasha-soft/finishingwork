package com.burnsale.finishingwork;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

public class HelpActivity extends AppCompatActivity {
    String h = "<html>\n" +
            "<head>\n" +
            "<meta charset=\"utf-8\">\n" +
            "<title>help</title>\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            "<h1>Как пользоваться?</h1>\n" +
            "<table>\n" +
            "\t<tr>\n" +
            "\t\t<td width=\"30%\"><h4>Шаг 1</h4></td>\n" +
            "\t\t<td width=\"70%\">Заполняем <b>Прайс-лист</b>. Кнопка внизу экрана или в меню. Вводим категория, тип работы, стоимость и за какой объем. </td>\n" +
            "\t</tr>\n" +
            "\t\t<tr>\n" +
            "\t\t<td width=\"30%\"><h4>Шаг 2</h4></td>\n" +
            "\t\t<td width=\"70%\">Добавляем <b>Заказ</b>. Вводим название. <i>Пример: ул. Юбилейная, 30</i></td>\n" +
            "\t</tr>\t\n" +
            "\t<tr>\n" +
            "\t\t<td width=\"30%\"><h4>Шаг 3</h4></td>\n" +
            "\t\t<td width=\"70%\">Добавляем <b>Объект</b>. Он делится на комнаты внути.<i>Пример: Офис состоит из 4 комнат, вписываете размер каждой комнаты.</i> Если только одна конмната в заказе, добавляете одну. Каждой комнате вписываем <b>длина, ширина и высота.</b></td>\n" +
            "\t</tr>\n" +
            "\t\t<tr>\n" +
            "\t\t<td width=\"30%\"><h4>Шаг 4</h4></td>\n" +
            "\t\t<td width=\"70%\">Выбираете какие работы будут производится, к данному объекту. <i>Пример: Покраска стен, укладка ламината</i>. Виды работ, берутся с указанных Вами в <b>прайс-листе</b> работ.</td>\n" +
            "\t</tr>\n" +
            "\t<tr>\n" +
            "\t\t<td width=\"30%\"><h4>Шаг 5</h4></td>\n" +
            "\t\t<td width=\"70%\">Производим расчет суммы на данном объекте.</td>\n" +
            "\t</tr>\n" +
            "</table>\n" +
            "\n" +
            "<center>Разработчик: Голик А.Г.</center>\n" +
            "<center><b>golik@list.ru</b></center>\n" +
            "</body>\n" +
            "</html>\n";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        WebView wHelp = (WebView) findViewById(R.id.webHelp);
        wHelp.loadData(h, "text/html; charset=UTF-8", null );

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
