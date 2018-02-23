# todos/views.py
from rest_framework.response import Response
from rest_framework import generics
from rest_framework import renderers
from rest_framework import views

from .models import Substance, Category, Unit
from . import util
from . import serializers


class CategoryView(generics.ListCreateAPIView):
    renderer_classes = [renderers.JSONRenderer]
    serializer_class = serializers.CategorySerializer

    def get_queryset(self):
        num_instance = self.request.query_params.get('num')
        if num_instance is not None:
            queryset = Category.objects.all()[:int(num_instance)]
        else:
            queryset = Category.objects.all()
        return queryset


class SubstanceView(generics.ListCreateAPIView):
    renderer_classes = [renderers.JSONRenderer]
    serializer_class = serializers.SubstanceSerializer

    def get_queryset(self):
        num_instance = self.request.query_params.get('num')
        if num_instance is not None:
            queryset = Substance.objects.all()[:int(num_instance)]
        else:
            queryset = Substance.objects.all()
        return queryset


class SubstanceInstanceView(generics.RetrieveAPIView):
    renderer_classes = [renderers.JSONRenderer]
    model = Substance
    serializer_class = serializers.SubstanceSerializer
    queryset = Substance.objects.all()


class CategoryInstanceView(generics.RetrieveAPIView):
    renderer_classes = [renderers.JSONRenderer]
    model = Category
    serializer_class = serializers.CategorySerializer
    queryset = Category.objects.all()


class UnitView(generics.ListCreateAPIView):
    renderer_classes = [renderers.JSONRenderer]
    serializer_class = serializers.UnitSerializer

    def get_queryset(self):
        num_instance = self.request.query_params.get('num')
        if num_instance is not None:
            queryset = Unit.objects.all()[:int(num_instance)]
        else:
            queryset = Unit.objects.all()
        return queryset


class UnitInstanceView(generics.RetrieveAPIView):
    renderer_classes = [renderers.JSONRenderer]
    model = Unit
    serializer_class = serializers.CategorySerializer
    queryset = Unit.objects.all()


class GetAvgPerDayView(views.APIView):
    # renderer_classes = [renderers.JSONRenderer]

    def get(self, request):
        category = request.query_params.get('category')
        days = request.query_params.get('days')
        queryset = util.get_avg_by_day(days, category)
        return Response(queryset)

