from rest_framework import serializers
from . import models


class SubstanceSerializer(serializers.ModelSerializer):
    class Meta:
        fields = (
            'id',
            # 'user',
            'unit',
            'value',
            'category',
            'record_date',
            'record_time',
            'notes'
        )
        model = models.Substance