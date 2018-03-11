from datetime import datetime, date, timedelta
from factory import DjangoModelFactory, Sequence, SubFactory
from factory.fuzzy import FuzzyInteger, FuzzyNaiveDateTime
from ..models import Substance, Category, Unit, User


class UserFactory(DjangoModelFactory):
    class Meta:
        model = User

    name = Sequence(lambda n: 'Category{0}'.format(n))


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

    unit = SubFactory(UnitFactory)
    user = SubFactory(UserFactory)
    value = FuzzyInteger(0, 8)
    category = SubFactory(CategoryFactory)
    record_date = date.today()
    record_time = FuzzyNaiveDateTime(datetime.now() - timedelta(hours=24))
    notes = 'This is a randomly generated dummy urine data'
