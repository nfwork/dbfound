/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nfwork.dbfound.json.converter;

/**
 * @author Andres Almiray
 */
@SuppressWarnings("unchecked")
public class NumberConverter implements Converter
{
   private Number defaultValue;
   private Class type;
   private boolean useDefault;

   public NumberConverter( Class type )
   {
      this( type, null );
   }

   public NumberConverter( Class type, Number defaultValue )
   {
      this.type = type;
      setDefaultValue( defaultValue );
   }

   public Object convert( Object value )
   {
      if( type == null || (!type.isPrimitive() && !Number.class.isAssignableFrom( type )) ){
         throw new ConversionException( "Type class must be a subclass of Number" );
      }

      if( value != null && type.isAssignableFrom( value.getClass() ) ){
         // no conversion needed
         return value;
      }

      String str = String.valueOf( value );
      Object result = null;

      if( !type.isPrimitive() ){
         // if empty string and class != primitive treat it like null
         if( value == null || str.length() == 0 || "null".equalsIgnoreCase( str ) ){
            return null;
         }
      }

      try{
         if( isDecimalNumber( type ) ){
            if( Float.class.isAssignableFrom( type ) || Float.TYPE == type ){
               result = new Float( new FloatConverter().convert( str ) );
            }else{
               result = new Double( new DoubleConverter().convert( str ) );
            }
         }else{
            if( Byte.class.isAssignableFrom( type ) || Byte.TYPE == type ){
               result = new Byte( new ByteConverter().convert( str ) );
            }else if( Short.class.isAssignableFrom( type ) || Short.TYPE == type ){
               result = new Short( new ShortConverter().convert( str ) );
            }else if( Integer.class.isAssignableFrom( type ) || Integer.TYPE == type ){
               result = new Integer( new IntConverter().convert( str ) );
            }else if( Long.class.isAssignableFrom( type ) || Long.TYPE == type ){
               result = new Long( new LongConverter().convert( str ) );
            }
         }
      }
      catch( ConversionException ce ){
         if( useDefault ){
            if( defaultValue != null ){
               if( type.isAssignableFrom( defaultValue.getClass() ) ){
                  return defaultValue;
               }else{
                  throw new ConversionException( "Can't assign default value: " + defaultValue );
               }
            }
            return null;
         }else{
            throw ce;
         }
      }

      return result;
   }

   public Number getDefaultValue()
   {
      return defaultValue;
   }

   public boolean isUseDefault()
   {
      return useDefault;
   }

   public void setDefaultValue( Number defaultValue )
   {
      this.defaultValue = defaultValue;
      this.useDefault = true;
   }

   public void setUseDefault( boolean useDefault )
   {
      this.useDefault = useDefault;
   }

   private boolean isDecimalNumber( Class type )
   {
      return (Double.class.isAssignableFrom( type ) || Float.class.isAssignableFrom( type )
            || Double.TYPE == type || Float.TYPE == type);
   }
}