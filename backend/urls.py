# pages/urls.py
from django.urls import path

from . import views

urlpatterns = [
    path('getavgperday', views.GetAvgPerDayView.as_view()),
    path('getallcategories', views.CategoryView.as_view()),
]
