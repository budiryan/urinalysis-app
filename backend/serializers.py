from rest_framework import serializers
from .models import Category, Substance, Unit


class CategorySerializer(serializers.ModelSerializer):
    class Meta:
        fields = '__all__'
        model = Category


class SubstanceSerializer(serializers.ModelSerializer):
    category_name = serializers.CharField(source='category.name')
    unit_name = serializers.CharField(source='unit.name')
    class Meta:
        fields = ('id', 'unit_name', 'value', 'category_name', 'record_date', 'record_time')
        model = Substance


class UnitSerializer(serializers.ModelSerializer):
    class Meta:
        fields = '__all__'
        model = Unit
