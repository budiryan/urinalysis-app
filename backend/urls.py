# pages/urls.py
from django.urls import path

from . import views

urlpatterns = [
    path('', views.Substance.as_view()),
]
