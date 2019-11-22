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

import java.lang.reflect.Array;

/**
 * Converts an array to a byte[].
 *
 * @author Andres Almiray
 */
@SuppressWarnings("unchecked")
public class ByteArrayConverter extends AbstractArrayConverter
{
   private static final Class BYTE_ARRAY_CLASS = byte[].class;
   private byte defaultValue;

   public ByteArrayConverter()
   {
      super( false );
   }

   public ByteArrayConverter( byte defaultValue )
   {
      super( true );
      this.defaultValue = defaultValue;
   }

   public Object convert( Object array )
   {
      if( array == null ){
         return null;
      }

      if( BYTE_ARRAY_CLASS.isAssignableFrom( array.getClass() ) ){
         // no conversion needed
         return (byte[]) array;
      }

      if( array.getClass()
            .isArray() ){
         int length = Array.getLength( array );
         int dims = getDimensions( array.getClass() );
         int[] dimensions = createDimensions( dims, length );
         Object result = Array.newInstance( byte.class, dimensions );
         ByteConverter converter = isUseDefault() ? new ByteConverter( defaultValue )
               : new ByteConverter();
         if( dims == 1 ){
            for( int index = 0; index < length; index++ ){
               Array.set( result, index,
                     new Byte( converter.convert( Array.get( array, index ) ) ) );
            }
         }else{
            for( int index = 0; index < length; index++ ){
               Array.set( result, index, convert( Array.get( array, index ) ) );
            }
         }
         return result;
      }else{
         throw new ConversionException( "argument is not an array: " + array.getClass() );
      }
   }

   public byte getDefaultValue()
   {
      return defaultValue;
   }

   public void setDefaultValue( byte defaultValue )
   {
      this.defaultValue = defaultValue;
      setUseDefault( true );
   }
}