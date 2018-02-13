from django.contrib import admin

# Register your models here.
from .models import Substance, Category, Unit, UserSettings, TimeStampedModel

admin.site.register(Substance)
admin.site.register(Category)
admin.site.register(Unit)
admin.site.register(UserSettings)
admin.site.register(TimeStampedModel)


