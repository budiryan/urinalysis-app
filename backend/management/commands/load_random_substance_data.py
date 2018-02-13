from datetime import date, timedelta

from django.core.management.base import BaseCommand
from django.db.models.base import ObjectDoesNotExist
from django.contrib.auth.models import User

from ...models import Substance, Category, Unit
from ...tests.factories import SubstanceFactory


class Command(BaseCommand):
    args = '<username> <substance type> <substance unit type>'
    help = 'Populate substance table with random dummy data.'

    def add_arguments(self, parser):
        parser.add_argument('username')
        parser.add_argument('substance_type')
        parser.add_argument('substance_unit')

    def handle(self, *args, **options):
        username = options['username']
        substance_type = options['substance_type'].lower()
        substance_unit = options['substance_unit'].lower()

        try:
            user = User.objects.get(username=username)
        except ObjectDoesNotExist:
            user = User.objects.create(username=username)
            user.first_name = 'Budi'
            user.last_name = 'Ryan'
            user.email = 'budiryan@gmail.com'
            user.set_password('demo')
            user.save()

        try:
            category = Category.objects.get(name=substance_type)
        except ObjectDoesNotExist:
            category = Category.objects.create(name=substance_type)

        try:
            unit = Unit.objects.get(name=substance_unit)
        except ObjectDoesNotExist:
            unit = Unit.objects.create(name=substance_unit)

        # Delete existing data.
        Substance.objects.filter(user=user).delete()

        end_date = date.today()
        start_date = end_date - timedelta(days=30)
        for i in self.get_date_list(start_date, end_date):
            for _ in range(4):
                SubstanceFactory(
                    user=user,
                    unit=unit,
                    category=category,
                    record_date=i
                )

    def get_date_list(cls, start, end):
        delta = end - start
        return [(start + timedelta(days=i)) for i in range(delta.days+1)]
