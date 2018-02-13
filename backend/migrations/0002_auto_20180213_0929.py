# Generated by Django 2.0.2 on 2018-02-13 09:29

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0001_initial'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='usersettings',
            name='unit',
        ),
        migrations.AddField(
            model_name='substance',
            name='unit',
            field=models.ForeignKey(default=0, on_delete=django.db.models.deletion.CASCADE, to='backend.Unit'),
            preserve_default=False,
        ),
        migrations.AlterField(
            model_name='unit',
            name='name',
            field=models.CharField(default='mg/dl', max_length=10, unique=True),
        ),
    ]
