# Generated by Django 2.0.2 on 2018-02-27 14:57

import datetime
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0007_auto_20180227_1456'),
    ]

    operations = [
        migrations.AlterField(
            model_name='substance',
            name='record_time',
            field=models.TimeField(default=datetime.time(14, 57, 28, 650785), verbose_name='Time'),
        ),
    ]
