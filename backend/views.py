# todos/views.py
from django.http import Http404
from rest_framework.response import Response
from rest_framework import generics, status
from rest_framework import renderers
from rest_framework import views
from rest_framework.views import APIView

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


class SubstanceList(APIView):
    """
    List all users, or create a new user.
    """
    def get(self, request, format=None):
        num_instance = request.query_params.get('num')
        category = request.query_params.get('category')

        substances = Substance.objects.all()

        if category is not None:
            substances = substances.filter(category__name=category)

        if num_instance is not None:
            substances = substances[:int(num_instance)]

        serializer = serializers.SubstanceSerializer(substances, many=True)
        return Response(serializer.data)

    def post(self, request, format=None):
        serializer = serializers.SubstanceSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)



class SubstanceDetail(APIView):
    """
    Retrieve, update or delete a user instance.
    """
    def get_object(self, pk):
        try:
            return Substance.objects.get(pk=pk)
        except Substance.DoesNotExist:
            raise Http404

    def get(self, request, pk, format=None):
        substance = self.get_object(pk)
        substance = serializers.SubstanceSerializer(substance)
        return Response(substance.data)

    def put(self, request, pk, format=None):
        serializer = serializers.SubstanceSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk, format=None):
        substance = self.get_object(pk)
        substance.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)



class CategoryInstanceView(generics.RetrieveAPIView):
    renderer_classes = [renderers.JSONRenderer]
    model = Category
    serializer_class = serializers.CategorySerializer
    queryset = Category.objects.all()


class CategoryDetail(APIView):
    """
    Retrieve, update or delete a user instance.
    """
    def get_object(self, pk):
        try:
            return Unit.objects.get(pk=pk)
        except Unit.DoesNotExist:
            raise Http404

    def get(self, request, pk, format=None):
        category = self.get_object(pk)
        category = serializers.CategorySerializer(category)
        return Response(category.data)

    def put(self, request, pk, format=None):
        serializer = serializers.CategorySerializer(data=request.DATA)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk, format=None):
        category = self.get_object(pk)
        category.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)


class CategoryList(APIView):
    """
    List all users, or create a new user.
    """
    def get(self, request, format=None):
        categories = Category.objects.all()

        serializer = serializers.CategorySerializer(categories, many=True)
        return Response(serializer.data)

    def post(self, request, format=None):
        serializer = serializers.CategorySerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class UnitList(APIView):
    """
    List all users, or create a new user.
    """
    def get(self, request, format=None):
        units = Unit.objects.all()

        serializer = serializers.UnitSerializer(units, many=True)
        return Response(serializer.data)

    def post(self, request, format=None):
        serializer = serializers.UnitSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class UnitDetail(APIView):
    """
    Retrieve, update or delete a user instance.
    """
    def get_object(self, pk):
        try:
            return Unit.objects.get(pk=pk)
        except Unit.DoesNotExist:
            raise Http404

    def get(self, request, pk, format=None):
        unit = self.get_object(pk)
        unit = serializers.UnitSerializer(unit)
        return Response(unit.data)

    def put(self, request, pk, format=None):
        serializer = serializers.UnitSerializer(data=request.DATA)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk, format=None):
        unit = self.get_object(pk)
        unit.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)


class GetAvgPerDayView(views.APIView):
    renderer_classes = [renderers.JSONRenderer]

    def get(self, request):
        category = request.query_params.get('category')
        queryset = util.get_avg_by_day(category)
        return Response(queryset)


class GetStats(views.APIView):
    renderer_classes = [renderers.JSONRenderer]

    def get(self, request):
        category = request.query_params.get('category')
        queryset = util.get_stats(category)
        return Response(queryset)
