from django.db import models
from django.core.validators import MinValueValidator, MaxValueValidator
import datetime
# from django.contrib.auth.models import User
from django.conf import settings
from timezone_field import TimeZoneField


# Create your models here.
class TimeStampedModel(models.Model):
    """
    Abstract base class that provides self-updating 'created' and 'modified'
    fields.
    """
    created = models.DateTimeField(auto_now_add=True)
    modified = models.DateTimeField(auto_now=True)


class SubstanceManager(models.Manager):
    def by_user(self, user, **kwargs):
        """
        Filter objects by the 'user' field.
        """
        return self.select_related()

    def by_date(self, start_date, end_date, **kwargs):
        """
        Filter objects by date range.
        """
        resultset = self.filter(
            record_date__gte=start_date,
            record_date__lte=end_date,
        )

        return resultset.order_by('-record_date', '-record_time')

    def by_category(self, start_date, end_date, **kwargs):
        """
        Group objects by category and take the count.
        """
        data = self.by_date(start_date, end_date)

        return data.values('category__name').annotate(count=models.Count('value')).order_by('category')

    def avg_by_category(self, start_date, end_date):
        """
        Group objects by category and take the average of the values.
        """
        data = self.by_date(start_date, end_date)

        return data.values('category__name').annotate(avg_value=models.Avg('value')).order_by('category')

    def avg_by_day(self, start_date, end_date, category):
        """
        Group objects by record date and take the average of the values.
        """
        print('start date: ', start_date)
        print('end date: ', end_date)
        data = self.by_date(start_date, end_date)
        return data.values('record_date').annotate(avg_value=models.Avg('value')).order_by('record_date') \
            .filter(category__name=category)


# Model for storing test result
class Substance(TimeStampedModel):
    objects = SubstanceManager()

    # user = models.ForeignKey(User, on_delete=models.CASCADE)
    unit = models.ForeignKey('Unit', on_delete=models.CASCADE)
    value = models.PositiveIntegerField(validators=[MaxValueValidator(54054),
                                                    MinValueValidator(0)])
    category = models.ForeignKey('Category', on_delete=models.CASCADE)
    record_date = models.DateField('Date')
    record_time = models.TimeField('Time')
    notes = models.TextField('Notes', null=False, blank=True, default='')

    def __unicode__(self):
        return str(self.value)

    class Meta:
        ordering = ['-record_date', '-record_time']


# Model for storing substance type. e.g.: Glucose, Urine Color, Ketone, Protein, etc
class Category(models.Model):
    name = models.CharField(unique=True, max_length=255)

    def __unicode__(self):
        return self.name


# Model for storing unit types, each type
class Unit(models.Model):
    name = models.CharField(unique=True, max_length=10, default='mg/dl')

    def __unicode__(self):
        return self.name

# class UserSettings(TimeStampedModel):
#     """
#     Model to store additional user settings and preferences. Extends User
#     model.
#     """
#     user = models.OneToOneField(User, on_delete=models.CASCADE, related_name='settings')
#
#     time_zone = TimeZoneField(default=settings.TIME_ZONE)
#
#     def username(self):
#         return self.user.username
