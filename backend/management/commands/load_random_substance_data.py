from datetime import date, timedelta

from django.core.management.base import BaseCommand
from django.db.models.base import ObjectDoesNotExist
from factory.fuzzy import FuzzyInteger

from ...models import Category, Unit, User
from ...tests.factories import SubstanceFactory


class Command(BaseCommand):
    args = '<substance type> <substance unit type>'
    help = 'Populate substance table with random dummy data.'

    def add_arguments(self, parser):
        parser.add_argument('username')
        parser.add_argument('upper_bound')
        parser.add_argument('substance_type')
        parser.add_argument('substance_unit')

    def handle(self, *args, **options):
        username = options['username']
        upper_bound = options['upper_bound']
        substance_type = options['substance_type']
        substance_unit = options['substance_unit']

        try:
            user = User.objects.get(name=username.lower())
        except ObjectDoesNotExist:
            user = User.objects.create(name=username.lower())

        try:
            category = Category.objects.get(name=substance_type.lower())
        except ObjectDoesNotExist:
            category = Category.objects.create(name=substance_type.lower())

        try:
            unit = Unit.objects.get(name=substance_unit.lower())
        except ObjectDoesNotExist:
            unit = Unit.objects.create(name=substance_unit.lower(), category=category)


        end_date = date.today()
        start_date = end_date - timedelta(days=7)
        for i in self.get_date_list(start_date, end_date):
            for _ in range(3):
                SubstanceFactory(
                    user=user,
                    unit=unit,
                    category=category,
                    record_date=i,
                    value = FuzzyInteger(0, int(upper_bound))
                )

    def get_date_list(cls, start, end):
        delta = end - start
        return [(start + timedelta(days=i)) for i in range(delta.days+1)]
