# Generated by Django 2.0.2 on 2018-02-27 14:10

import datetime
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0005_auto_20180226_0941'),
    ]

    operations = [
        migrations.AlterField(
            model_name='substance',
            name='record_date',
            field=models.DateField(default=datetime.date(2018, 2, 27), verbose_name='Date'),
        ),
        migrations.AlterField(
            model_name='substance',
            name='record_time',
            field=models.TimeField(default=datetime.time(14, 10, 3, 844716), verbose_name='Time'),
        ),
    ]