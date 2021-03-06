# todos/views.py
from django.http import Http404
from rest_framework.response import Response
from rest_framework import generics, status
from rest_framework import renderers
from rest_framework import views
from rest_framework.views import APIView

from .models import Substance, Category, Unit, User
from . import util
from . import serializers
import datetime

class SubstanceList(APIView):
    """
    List all users, or create a new user.
    """
    renderer_classes = [renderers.JSONRenderer]
    def get(self, request, format=None):
        num_instance = request.query_params.get('num')
        category = request.query_params.get('category')
        user = request.query_params.get('user')

        substances = Substance.objects.all()

        if category is not None:
            substances = substances.filter(category__name=category)

        if user is not None:
            substances = substances.filter(user__name=user)

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
    renderer_classes = [renderers.JSONRenderer]
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


class CategoryDetail(APIView):
    """
    Retrieve, update or delete a user instance.
    """
    renderer_classes = [renderers.JSONRenderer]
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
    renderer_classes = [renderers.JSONRenderer]
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
    List all users, or create a new unit detail.
    """
    renderer_classes = [renderers.JSONRenderer]
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
    Retrieve, update or delete a unit instance.
    """
    renderer_classes = [renderers.JSONRenderer]
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


class UserList(APIView):
    """
    List all users, or create a new user.
    """
    renderer_classes = [renderers.JSONRenderer]
    def get(self, request, format=None):
        users = User.objects.all()

        serializer = serializers.UserSerializer(users, many=True)
        return Response(serializer.data)

    def post(self, request, format=None):
        serializer = serializers.UserSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class UserDetail(APIView):
    """
    Retrieve, update or delete a user instance.
    """
    renderer_classes = [renderers.JSONRenderer]
    def get_object(self, pk):
        try:
            return User.objects.get(pk=pk)
        except User.DoesNotExist:
            raise Http404

    def get(self, request, pk, format=None):
        user = self.get_object(pk)
        user = serializers.UserSerializer(user)
        return Response(user.data)

    def put(self, request, pk, format=None):
        serializer = serializers.UserSerializer(data=request.DATA)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk, format=None):
        user = self.get_object(pk)
        user.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)

class GetUserCategory(views.APIView):
    renderer_classes = [renderers.JSONRenderer]

    def get(self, request):
        data = {'users': [], 'categories': []}
        users = User.objects.all()
        categories = Category.objects.all()
        data['users'] = [u.name for u in users]
        data['categories'] = [c.name for c in categories]
        return Response(data)


class GetUserCategoryUnit(views.APIView):
    renderer_classes = [renderers.JSONRenderer]

    def get(self, request):
        data = {'users': [], 'categories':[], 'units': []}
        users = User.objects.all()
        categories = Category.objects.all()
        units = Unit.objects.all()
        data['users'] = users.values()
        data['units'] = units.values()
        data['categories'] = categories.values()
        return Response(data)


class GetAvgPerDayView(views.APIView):
    renderer_classes = [renderers.JSONRenderer]

    def get(self, request):
        category = request.query_params.get('category')
        user = request.query_params.get('user')
        queryset = util.get_avg_by_day(category, user)
        return Response(queryset)


class GetStats(views.APIView):
    renderer_classes = [renderers.JSONRenderer]

    def get(self, request):
        category = request.query_params.get('category')
        user = request.query_params.get('user')
        queryset = util.get_stats(category, user)
        return Response(queryset)
    

class SuggestedWaterIntake(views.APIView):
    renderer_classes = [renderers.JSONRenderer]


    def get(self, request):
        user = request.query_params.get('user')
        data = {'suggested_water_intake': '', 'previous_test_date': '', 'previous_test_time': '',
                'current_test_date': '', 'current_test_time': '', 'urine_color_difference': ''}

        urine_color_tests = Substance.objects.all().filter(category__name='urine color', user__name=user)

        current_test = urine_color_tests[0]

        previous_day = datetime.datetime.combine(current_test.record_date - datetime.timedelta(hours=24), current_test.record_time)

        # Calculate time difference between previous_day and the entire test set
        smallest_time_difference = None
        previous_test = None

        for test in urine_color_tests:
            test_date_time = datetime.datetime.combine(test.record_date, test.record_time)
            current_time_difference = abs(test_date_time - previous_day)

            if (smallest_time_difference is None) or (current_time_difference < smallest_time_difference):
                smallest_time_difference = current_time_difference
                previous_test = test

        urine_color_difference = current_test.value - previous_test.value

        data['previous_test_date'] = previous_test.record_date
        data['previous_test_time'] = previous_test.record_time
        data['current_test_date'] = current_test.record_date
        data['current_test_time'] = current_test.record_time
        data['urine_color_difference'] = urine_color_difference

        # Knowing the 24 hour urine color difference, calculate the suggested water intake based on the formula
        if urine_color_difference < -4.0:
            data['suggested_water_intake'] = 1500 + (-4.0 - urine_color_difference) * 100

        elif (urine_color_difference >= -4.0) and (urine_color_difference < -3.0):
            data['suggested_water_intake'] = 1400 + (-3.0 - urine_color_difference) * 100

        elif (urine_color_difference >= -3.0) and (urine_color_difference < -2.0):
            data['suggested_water_intake'] = 1260 + (-2.0 - urine_color_difference) * 140

        elif (urine_color_difference >= -2.0) and (urine_color_difference < -1.0):
            data['suggested_water_intake'] = 500 + (-1 - urine_color_difference) * 760

        elif (urine_color_difference >= -1.0) and (urine_color_difference < 0.0):
            data['suggested_water_intake'] = -300 - (urine_color_difference * 800)

        elif (urine_color_difference >= 0.0) and (urine_color_difference < 1.0):
            data['suggested_water_intake'] = -300 - (urine_color_difference * 200)

        elif (urine_color_difference >= 1.0) and (urine_color_difference < 2.0):
            data['suggested_water_intake'] = -500 - (urine_color_difference - 1) * 800

        elif (urine_color_difference >= 2.0) and (urine_color_difference < 3.0):
            data['suggested_water_intake'] = -1300 - (urine_color_difference - 2) * 100

        elif urine_color_difference >= 3.0:
            data['suggested_water_intake'] = -1400 * (urine_color_difference - 3) * 100

        return Response(data)
