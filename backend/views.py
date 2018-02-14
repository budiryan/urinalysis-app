# todos/views.py
from rest_framework import generics
from rest_framework import renderers

from . import models
from . import serializers


class Substance(generics.ListCreateAPIView):
    renderer_classes = [renderers.JSONRenderer]
    queryset = models.Substance.objects.all()
    serializer_class = serializers.SubstanceSerializer
