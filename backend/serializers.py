from rest_framework import serializers
from .models import Category, Substance, Unit


class CategorySerializer(serializers.ModelSerializer):
    class Meta:
        fields = '__all__'
        model = Category


class SubstanceSerializer(serializers.ModelSerializer):
    category_name = serializers.CharField(source='category.name', required=False)
    unit_name = serializers.CharField(source='unit.name', required=False)
    record_date = serializers.DateField(required=False)
    record_time = serializers.TimeField(format="%H:%M:%S", required=False)
    user_name = serializers.CharField(source='user.name', required=False)

    class Meta:
        fields = ('id', 'unit', 'unit_name', 'user', 'user_name', 'value', 'category',
                  'category_name', 'record_date', 'record_time', 'notes')
        model = Substance


class UnitSerializer(serializers.ModelSerializer):
    category_name = serializers.CharField(source='category.name', required=False)
    class Meta:
        fields = ('id', 'name', 'category', 'category_name')
        model = Unit
