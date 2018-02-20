from rest_framework import serializers
from .models import Category, Substance, Unit


class CategorySerializer(serializers.ModelSerializer):
    class Meta:
        fields = '__all__'
        model = Category


class SubstanceSerializer(serializers.ModelSerializer):
    class Meta:
        fields = '__all__'
        model = Substance


class UnitSerializer(serializers.ModelSerializer):
    class Meta:
        fields = '__all__'
        model = Unit
