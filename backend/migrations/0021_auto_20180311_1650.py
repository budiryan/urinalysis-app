# Generated by Django 2.0.2 on 2018-03-11 08:50

import backend.models
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0020_auto_20180311_1641'),
    ]

    operations = [
        migrations.AlterField(
            model_name='substance',
            name='record_time',
            field=models.TimeField(auto_now_add=backend.models.get_current_time, verbose_name='Time'),
        ),
    ]
