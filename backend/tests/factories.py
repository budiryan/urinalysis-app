from datetime import datetime, date, timedelta

from factory import DjangoModelFactory, Sequence, SubFactory
from factory.fuzzy import FuzzyInteger, FuzzyNaiveDateTime

from django.contrib.auth.models import User

# from ..models import Substance, Category, UserSettings, Unit
from ..models import Substance, Category, Unit


# class UserFactory(DjangoModelFactory):
#     class Meta:
#         model = User
#
#     username = Sequence(lambda n: 'user{0}'.format(n))
#
#
# class UserSettingsFactory(DjangoModelFactory):
#     class Meta:
#         model = UserSettings
#
#     user = SubFactory(UserFactory)


class CategoryFactory(DjangoModelFactory):
    class Meta:
        model = Category

    name = Sequence(lambda n: 'Category{0}'.format(n))


class UnitFactory(DjangoModelFactory):
    class Meta:
        model = Unit

    name = Sequence(lambda n: 'Category{0}'.format(n))
    category = SubFactory(CategoryFactory)


class SubstanceFactory(DjangoModelFactory):
    class Meta:
        model = Substance

    # user = SubFactory(UserFactory)
    unit = SubFactory(UnitFactory)
    value = FuzzyInteger(0, 300)
    category = SubFactory(CategoryFactory)
    record_date = date.today()
    record_time = FuzzyNaiveDateTime(datetime.now() - timedelta(hours=24))
    notes = 'This is a randomly generated dummy urine data'





