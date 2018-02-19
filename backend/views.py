# todos/views.py
from rest_framework.response import Response
from rest_framework import generics
from rest_framework import renderers
from rest_framework import views

from .models import Substance, Category
from . import util
from . import serializers


class CategoryView(generics.ListCreateAPIView):
    renderer_classes = [renderers.JSONRenderer]
    queryset = Category.objects.all()
    serializer_class = serializers.CategorySerializer


class GetAvgPerDayView(views.APIView):
    renderer_classes = [renderers.JSONRenderer]

    def get(self, request):
        category = request.query_params.get('category')
        days = request.query_params.get('days')
        queryset = util.get_avg_by_day(days, category)
        return Response(queryset)
