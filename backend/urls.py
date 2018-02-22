# pages/urls.py
from django.conf.urls import url
from django.urls import path

from . import views

urlpatterns = [
    path('getavgperday', views.GetAvgPerDayView.as_view()),
    path('category', views.CategoryView.as_view()),
    path('category/<int:pk>/', views.CategoryInstanceView.as_view()),
    path('substance', views.SubstanceView.as_view()),
    path('substance/<int:pk>/', views.SubstanceInstanceView.as_view()),
    path('unit/<int:pk>/', views.UnitInstanceView.as_view()),
]
