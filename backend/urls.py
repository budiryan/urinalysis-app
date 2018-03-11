# pages/urls.py
from django.conf.urls import url
from django.urls import path

from . import views

urlpatterns = [
    path('getavgperday', views.GetAvgPerDayView.as_view()),
    path('getstats', views.GetStats.as_view()),
    path('category', views.CategoryList.as_view()),
    path('category/<int:pk>/', views.CategoryDetail.as_view()),
    path('substance', views.SubstanceList.as_view()),
    path('substance/<int:pk>/', views.SubstanceDetail.as_view()),
    path('unit/', views.UnitList.as_view()),
    path('unit/<int:pk>/', views.UnitDetail.as_view()),
    path('user', views.UserList.as_view()),
    path('user/<int:pk>/', views.UserDetail.as_view()),
]
