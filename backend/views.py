# todos/views.py
from rest_framework import generics

from . import models
from . import serializers


class Substance(generics.ListCreateAPIView):
    queryset = models.Substance.objects.all()
    serializer_class = serializers.SubstanceSerializer
